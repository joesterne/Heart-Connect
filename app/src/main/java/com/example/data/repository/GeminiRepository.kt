package com.example.data.repository

import com.example.BuildConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val tools: List<Tool>? = null,
    val systemInstruction: Content? = null
)

@Serializable
data class Tool(
    val googleSearch: JsonObject? = null
)

@Serializable
data class Content(
    val parts: List<Part>,
    val role: String? = null
)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String
)

@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
data class GenerationConfig(
    val thinkingConfig: ThinkingConfig? = null,
    val temperature: Float? = null
)

@Serializable
data class ThinkingConfig(
    val thinkingLevel: String
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null
)

interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @retrofit2.http.Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val json = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

class GeminiRepository {
    suspend fun getCounselingResponse(prompt: String, conversationHistory: List<Content>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val model = "gemini-3.1-pro-preview"
        val request = GenerateContentRequest(
            contents = conversationHistory + Content(parts = listOf(Part(text = prompt)), role = "user"),
            generationConfig = GenerationConfig(
                thinkingConfig = ThinkingConfig(thinkingLevel = "HIGH")
            ),
            systemInstruction = Content(parts = listOf(Part(text = "You are an empathetic, highly trained medical counselor for patients on the heart transplant waiting list. Be supportive, informative, and compassionate.")))
        )
        try {
            val response = RetrofitClient.service.generateContent(model, apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No response from counselor."
        } catch (e: Exception) {
            "Error connecting to counselor: ${e.localizedMessage}"
        }
    }

    suspend fun transcribeAudio(base64Audio: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val model = "gemini-1.5-flash"
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(
                Part(text = "Extract the mood on a scale of 1-5 (where 1 is terrible, 5 is great), a list of symptoms, and any other notes from this audio. Return ONLY the format: MOOD_NUMBER|||SYMPTOMS_STRING|||NOTES_STRING. Example: 3|||Fatigue, Headache|||Had a long day today."),
                Part(inlineData = InlineData(mimeType = "audio/mp4", data = base64Audio))
            ), role = "user"))
        )
        try {
            val response = RetrofitClient.service.generateContent(model, apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Could not transcribe audio."
        } catch (e: Exception) {
            "Error transcribing audio: ${e.localizedMessage}"
        }
    }

    suspend fun searchMedicalResources(query: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val model = "gemini-1.5-flash"
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = "Search for the latest up-to-date resources and advice for heart transplant waitlist patients regarding: $query")), role = "user")),
            tools = listOf(Tool(googleSearch = JsonObject(emptyMap())))
        )
        try {
            val response = RetrofitClient.service.generateContent(model, apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No resources found."
        } catch (e: Exception) {
            "Error searching resources: ${e.localizedMessage}"
        }
    }
}

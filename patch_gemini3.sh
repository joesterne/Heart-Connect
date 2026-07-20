sed -i '/suspend fun searchMedicalResources/i \
    suspend fun transcribeAudio(base64Audio: String): String = withContext(Dispatchers.IO) {\
        val apiKey = BuildConfig.GEMINI_API_KEY\
        val model = "gemini-3.5-flash"\
        val request = GenerateContentRequest(\
            contents = listOf(Content(parts = listOf(\
                Part(text = "Transcribe this audio clip accurately. Only return the transcribed text without any other comments."),\
                Part(inlineData = InlineData(mimeType = "audio/mp4", data = base64Audio))\
            ), role = "user"))\
        )\
        try {\
            val response = RetrofitClient.service.generateContent(model, apiKey, request)\
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Could not transcribe audio."\
        } catch (e: Exception) {\
            "Error transcribing audio: ${e.localizedMessage}"\
        }\
    }\
' app/src/main/java/com/example/data/repository/GeminiRepository.kt

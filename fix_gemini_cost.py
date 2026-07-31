with open("app/src/main/java/com/example/data/repository/GeminiRepository.kt", "r") as f:
    content = f.read()

old_counseling = """    suspend fun getCounselingResponse(prompt: String, conversationHistory: List<Content>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val model = "gemini-3.1-pro-preview"
        val request = GenerateContentRequest(
            contents = conversationHistory, // The conversationHistory already includes the latest prompt
            generationConfig = GenerationConfig(
                thinkingConfig = ThinkingConfig(thinkingLevel = "HIGH")
            ),
            systemInstruction = Content(parts = listOf(Part(text = "You are an empathetic, highly trained medical counselor for patients on the heart transplant waiting list. Be supportive, informative, and compassionate.")))
        )"""

new_counseling = """    suspend fun getCounselingResponse(prompt: String, conversationHistory: List<Content>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val model = "gemini-1.5-flash"
        val request = GenerateContentRequest(
            contents = conversationHistory, // The conversationHistory already includes the latest prompt
            systemInstruction = Content(parts = listOf(Part(text = "You are an empathetic, highly trained medical counselor for patients on the heart transplant waiting list. Be supportive, informative, and compassionate.")))
        )"""

content = content.replace(old_counseling, new_counseling)

with open("app/src/main/java/com/example/data/repository/GeminiRepository.kt", "w") as f:
    f.write(content)

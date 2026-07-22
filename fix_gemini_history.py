with open("app/src/main/java/com/example/data/repository/GeminiRepository.kt", "r") as f:
    content = f.read()

old_code = """    suspend fun getCounselingResponse(prompt: String, conversationHistory: List<Content>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val model = "gemini-3.1-pro-preview"
        val request = GenerateContentRequest(
            contents = conversationHistory + Content(parts = listOf(Part(text = prompt)), role = "user"),"""

new_code = """    suspend fun getCounselingResponse(prompt: String, conversationHistory: List<Content>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val model = "gemini-3.1-pro-preview"
        val request = GenerateContentRequest(
            contents = conversationHistory, // The conversationHistory already includes the latest prompt"""

content = content.replace(old_code, new_code)

with open("app/src/main/java/com/example/data/repository/GeminiRepository.kt", "w") as f:
    f.write(content)

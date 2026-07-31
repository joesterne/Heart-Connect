with open("app/src/main/java/com/example/data/repository/GeminiRepository.kt", "r") as f:
    content = f.read()

old_counseling = """    suspend fun getCounselingResponse(prompt: String, conversationHistory: List<Content>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val model = "gemini-1.5-flash"
        val request = GenerateContentRequest(
            contents = conversationHistory, // The conversationHistory already includes the latest prompt
            systemInstruction = Content(parts = listOf(Part(text = "You are an empathetic, highly trained medical counselor for patients on the heart transplant waiting list. Be supportive, informative, and compassionate.")))
        )"""

new_counseling = """    suspend fun getCounselingResponse(prompt: String, conversationHistory: List<Content>, customSystemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val model = "gemini-1.5-flash"
        val instructionText = customSystemInstruction ?: "You are an empathetic, highly trained medical counselor for patients on the heart transplant waiting list. Be supportive, informative, and compassionate."
        val request = GenerateContentRequest(
            contents = conversationHistory, // The conversationHistory already includes the latest prompt
            systemInstruction = Content(parts = listOf(Part(text = instructionText)))
        )"""

content = content.replace(old_counseling, new_counseling)

with open("app/src/main/java/com/example/data/repository/GeminiRepository.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/viewmodel/AppViewModel.kt", "r") as f:
    content = f.read()

old_call = """            val customRepository = GeminiRepository()
            // Request dynamic reply
            val replyText = try {
                customRepository.getCounselingResponse(prompt, geminiHistory)
            } catch (e: Exception) {"""

new_call = """            // Request dynamic reply
            val replyText = try {
                geminiRepository.getCounselingResponse(prompt, geminiHistory, systemInstructions)
            } catch (e: Exception) {"""

content = content.replace(old_call, new_call)

with open("app/src/main/java/com/example/viewmodel/AppViewModel.kt", "w") as f:
    f.write(content)

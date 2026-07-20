sed -i '/data class Part(/i \@Serializable\ndata class InlineData(\n    val mimeType: String,\n    val data: String\n)\n' app/src/main/java/com/example/data/repository/GeminiRepository.kt

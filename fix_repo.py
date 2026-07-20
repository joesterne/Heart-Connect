with open("app/src/main/java/com/example/data/repository/GeminiRepository.kt", "r") as f:
    content = f.read()

content = content.replace("@Serializable\n@Serializable", "@Serializable")

with open("app/src/main/java/com/example/data/repository/GeminiRepository.kt", "w") as f:
    f.write(content)

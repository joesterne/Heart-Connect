with open("app/src/main/java/com/example/data/repository/GeminiRepository.kt", "r") as f:
    content = f.read()

cert_pinner_code = """    private val certificatePinner = okhttp3.CertificatePinner.Builder()
        .add("generativelanguage.googleapis.com", "sha256/hxqRlPTu1bMS/0DITB1SSu0vd4u/8l8TjPgfaAp63Gc=") // Root CA for Google
        .add("generativelanguage.googleapis.com", "sha256/Y9mvm0exBk1JoQ57f9Vm28jKo5lFm/woKcVxrYfl8YA=") // Backup
        .add("generativelanguage.googleapis.com", "sha256/C5+lpZ7tcVwmwQIMcRtPbsQtWLABXhQzejna0wHFr8M=") // Backup
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .certificatePinner(certificatePinner)"""

if "certificatePinner(" not in content:
    content = content.replace("private val okHttpClient = OkHttpClient.Builder()", cert_pinner_code)

with open("app/src/main/java/com/example/data/repository/GeminiRepository.kt", "w") as f:
    f.write(content)

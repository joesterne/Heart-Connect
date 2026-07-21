with open("app/src/main/java/com/example/viewmodel/AppViewModel.kt", "r") as f:
    content = f.read()

import_secure_repo = "import com.example.data.repository.SecureStorageRepository"
if import_secure_repo not in content:
    content = content.replace("import com.example.data.repository.FirestoreRepository", import_secure_repo + "\nimport com.example.data.repository.FirestoreRepository")

secure_repo_init = "private val firestoreRepository = FirestoreRepository(application)\n    private val secureStorageRepository = SecureStorageRepository(application)"
if "private val secureStorageRepository" not in content:
    content = content.replace("private val firestoreRepository = FirestoreRepository(application)", secure_repo_init)

secure_backup_func = """    fun backupLogsSecurely() {
        val currentLogs = _userProfile.value?.dailyLogs ?: emptyList()
        val logsStr = currentLogs.joinToString(";;;") { "${it.id}|${it.timestamp}|${it.mood}|${it.notes}|${it.energyLevel}" }
        secureStorageRepository.saveEncryptedFile("daily_logs_backup.enc", logsStr)
    }

    fun restoreLogsSecurely() {
        val logsStr = secureStorageRepository.readEncryptedFile("daily_logs_backup.enc")
        if (logsStr != null && logsStr.isNotBlank()) {
            val dailyLogs = logsStr.split(";;;").mapNotNull { logStr ->
                val parts = logStr.split("|")
                if (parts.size == 5) {
                    com.example.data.model.DailyLog(parts[0], parts[1].toLong(), parts[2], parts[3], parts[4].toInt())
                } else null
            }
            _userProfile.value = _userProfile.value?.copy(dailyLogs = dailyLogs)
            sharedPrefs.edit().putString("profile_daily_logs", logsStr).apply()
        }
    }"""

if "fun backupLogsSecurely" not in content:
    content = content.replace("fun saveProfile(", secure_backup_func + "\n\n    fun saveProfile(")

with open("app/src/main/java/com/example/viewmodel/AppViewModel.kt", "w") as f:
    f.write(content)

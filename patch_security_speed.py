import re

# Update SecureStorageRepository
with open('app/src/main/java/com/example/data/repository/SecureStorageRepository.kt', 'r') as f:
    repo_content = f.read()

repo_content = repo_content.replace('import java.io.File', 'import java.io.File\nimport kotlinx.coroutines.Dispatchers\nimport kotlinx.coroutines.withContext')

repo_content = repo_content.replace(
    'fun saveEncryptedFile(filename: String, content: String) {',
    'suspend fun saveEncryptedFile(filename: String, content: String) = withContext(Dispatchers.IO) {'
)

repo_content = repo_content.replace(
    'fun readEncryptedFile(filename: String): String? {',
    'suspend fun readEncryptedFile(filename: String): String? = withContext(Dispatchers.IO) {'
)

repo_content = repo_content.replace(
    'return null',
    'return@withContext null'
)

repo_content = repo_content.replace(
    'return try {',
    'return@withContext try {'
)

repo_content = repo_content.replace(
    'fun deleteEncryptedFile(filename: String) {',
    'suspend fun deleteEncryptedFile(filename: String) = withContext(Dispatchers.IO) {'
)

with open('app/src/main/java/com/example/data/repository/SecureStorageRepository.kt', 'w') as f:
    f.write(repo_content)

# Update AppViewModel
with open('app/src/main/java/com/example/viewmodel/AppViewModel.kt', 'r') as f:
    vm_content = f.read()

old_backup = """    fun backupLogsSecurely() {
        val currentLogs = _userProfile.value?.dailyLogs ?: emptyList()
        val logsStr = currentLogs.joinToString(";;;") { "${it.id}|||${it.timestamp}|||${it.mood}|||${it.symptoms}|||${it.notes}" }
        secureStorageRepository.saveEncryptedFile("daily_logs_backup.enc", logsStr)
    }

    fun restoreLogsSecurely() {
        val logsStr = secureStorageRepository.readEncryptedFile("daily_logs_backup.enc")
        if (logsStr != null && logsStr.isNotBlank()) {
            val dailyLogs = logsStr.split(";;;").mapNotNull { logStr ->
                val parts = logStr.split("|||")
                if (parts.size == 5) {
                    com.example.data.model.DailyLog(parts[0], parts[1].toLong(), parts[2].toInt(), parts[3], parts[4])
                } else null
            }
            viewModelScope.launch {
                dailyLogs.forEach { logRepository.insertLog(it) }
                _notificationEvent.emit("Logs restored successfully.")
            }
        }
    }"""

new_backup = """    fun backupLogsSecurely() {
        viewModelScope.launch {
            val currentLogs = _userProfile.value?.dailyLogs ?: emptyList()
            val logsStr = currentLogs.joinToString(";;;") { "${it.id}|||${it.timestamp}|||${it.mood}|||${it.symptoms}|||${it.notes}" }
            secureStorageRepository.saveEncryptedFile("daily_logs_backup.enc", logsStr)
            _notificationEvent.emit("Logs backed up securely.")
        }
    }

    fun restoreLogsSecurely() {
        viewModelScope.launch {
            val logsStr = secureStorageRepository.readEncryptedFile("daily_logs_backup.enc")
            if (logsStr != null && logsStr.isNotBlank()) {
                val dailyLogs = logsStr.split(";;;").mapNotNull { logStr ->
                    val parts = logStr.split("|||")
                    if (parts.size == 5) {
                        com.example.data.model.DailyLog(parts[0], parts[1].toLong(), parts[2].toInt(), parts[3], parts[4])
                    } else null
                }
                dailyLogs.forEach { logRepository.insertLog(it) }
                _notificationEvent.emit("Logs restored successfully.")
            } else {
                _notificationEvent.emit("No backup found or backup empty.")
            }
        }
    }"""

vm_content = vm_content.replace(old_backup, new_backup)

with open('app/src/main/java/com/example/viewmodel/AppViewModel.kt', 'w') as f:
    f.write(vm_content)

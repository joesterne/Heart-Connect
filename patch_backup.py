import re
with open('app/src/main/java/com/example/viewmodel/AppViewModel.kt', 'r') as f:
    content = f.read()

backup_restore_old = """    fun backupLogsSecurely() {
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
            _userProfile.value = _userProfile.value?.copy(dailyLogs = dailyLogs)
            sharedPrefs.edit().putString("profile_daily_logs", logsStr).apply()
        }
    }"""
    
backup_restore_new = """    fun backupLogsSecurely() {
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
    
content = content.replace(backup_restore_old, backup_restore_new)

with open('app/src/main/java/com/example/viewmodel/AppViewModel.kt', 'w') as f:
    f.write(content)

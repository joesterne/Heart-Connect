with open("app/src/main/java/com/example/viewmodel/AppViewModel.kt", "r") as f:
    content = f.read()

old_backup = """    fun backupLogsSecurely() {
        val currentLogs = _userProfile.value?.dailyLogs ?: emptyList()
        val logsStr = currentLogs.joinToString(";;;") { "${it.id}|${it.timestamp}|${it.mood}|${it.symptoms}|${it.notes}" }
        secureStorageRepository.saveEncryptedFile("daily_logs_backup.enc", logsStr)
    }

    fun restoreLogsSecurely() {
        val logsStr = secureStorageRepository.readEncryptedFile("daily_logs_backup.enc")
        if (logsStr != null && logsStr.isNotBlank()) {
            val dailyLogs = logsStr.split(";;;").mapNotNull { logStr ->
                val parts = logStr.split("|")
                if (parts.size == 5) {
                    com.example.data.model.DailyLog(parts[0], parts[1].toLong(), parts[2].toInt(), parts[3], parts[4])
                } else null
            }
            _userProfile.value = _userProfile.value?.copy(dailyLogs = dailyLogs)
            sharedPrefs.edit().putString("profile_daily_logs", logsStr).apply()
        }
    }"""

new_backup = """    fun backupLogsSecurely() {
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

content = content.replace(old_backup, new_backup)

old_transcribe = """            if (parts.size >= 3) {
                val extractedMood = parts[0].trim().toIntOrNull() ?: mood
                val extractedSymptoms = parts[1].trim()
                val extractedNotes = parts[2].trim()
                addDailyLog(extractedMood, extractedSymptoms, extractedNotes)
            } else {
                addDailyLog(mood, symptoms, "[Audio Transcript]: $transcription")
            }
            addDailyLog(mood, symptoms, "[Audio Transcript]: $transcription")
            _isTranscribing.value = false"""

new_transcribe = """            if (parts.size >= 3) {
                val extractedMood = parts[0].trim().toIntOrNull() ?: mood
                val extractedSymptoms = parts[1].trim()
                val extractedNotes = parts[2].trim()
                addDailyLog(extractedMood, extractedSymptoms, extractedNotes)
            } else {
                addDailyLog(mood, symptoms, "[Audio Transcript]: $transcription")
            }
            _isTranscribing.value = false"""

content = content.replace(old_transcribe, new_transcribe)

with open("app/src/main/java/com/example/viewmodel/AppViewModel.kt", "w") as f:
    f.write(content)

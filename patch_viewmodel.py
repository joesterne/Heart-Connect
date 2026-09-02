import re
with open('app/src/main/java/com/example/viewmodel/AppViewModel.kt', 'r') as f:
    content = f.read()

# Add database init
db_init = """    private val database = androidx.room.Room.databaseBuilder(
        application,
        com.example.data.database.AppDatabase::class.java, "heartconnect-db"
    ).build()
    private val logRepository = com.example.data.repository.LogRepository(database.dailyLogDao())
"""
content = content.replace('    private val geminiRepository = GeminiRepository()', '    private val geminiRepository = GeminiRepository()\n' + db_init)

# Replace addDailyLog
add_log_old = """    fun addDailyLog(mood: Int, symptoms: String, notes: String) {
        val currentProfile = _userProfile.value ?: return
        val newLog = com.example.data.model.DailyLog(
            id = "log_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            mood = mood,
            symptoms = symptoms.replace("|||", "").replace(";;;", ""),
            notes = notes.replace("|||", "").replace(";;;", "")
        )
        val newLogs = currentProfile.dailyLogs.toMutableList().apply { add(0, newLog) }
        
        val logsStr = newLogs.joinToString(";;;") { log ->
            "${log.id}|||${log.timestamp}|||${log.mood}|||${log.symptoms}|||${log.notes}"
        }
        
        sharedPrefs.edit().putString("profile_daily_logs", logsStr).apply()
        _userProfile.value = currentProfile.copy(dailyLogs = newLogs, badges = getBadgesForProfile(currentProfile.isAvailableForMentorship, newLogs.size, currentProfile.journeyPhase))
    }"""
add_log_new = """    fun addDailyLog(mood: Int, symptoms: String, notes: String) {
        val newLog = com.example.data.model.DailyLog(
            id = "log_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            mood = mood,
            symptoms = symptoms.replace("|||", "").replace(";;;", ""),
            notes = notes.replace("|||", "").replace(";;;", "")
        )
        viewModelScope.launch {
            logRepository.insertLog(newLog)
            _notificationEvent.emit("Daily log saved successfully.")
        }
    }"""
content = content.replace(add_log_old, add_log_new)

# Remove sharedPrefs daily logs loading from init
init_old = """        val dailyLogsStr = sharedPrefs.getString("profile_daily_logs", "") ?: ""
        val dailyLogs = if (dailyLogsStr.isNotBlank()) {
            dailyLogsStr.split(";;;").mapNotNull { logStr ->
                val parts = logStr.split("|||")
                if (parts.size == 5) {
                    com.example.data.model.DailyLog(
                        id = parts[0],
                        timestamp = parts[1].toLongOrNull() ?: 0L,
                        mood = parts[2].toIntOrNull() ?: 3,
                        symptoms = parts[3],
                        notes = parts[4]
                    )
                } else null
            }
        } else emptyList()

        _userProfile.value = Profile(id = "me", name = name, age = age, location = location, medicalHistory = medicalHistory, aboutMe = aboutMe, journeyPhase = journeyPhase, isAvailableForMentorship = isAvailableForMentorship, dailyLogs = dailyLogs, badges = getBadgesForProfile(isAvailableForMentorship, dailyLogs.size, journeyPhase))"""
init_new = """        _userProfile.value = Profile(id = "me", name = name, age = age, location = location, medicalHistory = medicalHistory, aboutMe = aboutMe, journeyPhase = journeyPhase, isAvailableForMentorship = isAvailableForMentorship, dailyLogs = emptyList(), badges = getBadgesForProfile(isAvailableForMentorship, 0, journeyPhase))

        viewModelScope.launch {
            logRepository.allLogs.collect { logs ->
                val current = _userProfile.value
                if (current != null) {
                    _userProfile.value = current.copy(
                        dailyLogs = logs,
                        badges = getBadgesForProfile(current.isAvailableForMentorship, logs.size, current.journeyPhase)
                    )
                }
            }
        }"""
content = content.replace(init_old, init_new)

with open('app/src/main/java/com/example/viewmodel/AppViewModel.kt', 'w') as f:
    f.write(content)

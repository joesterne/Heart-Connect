import re
with open('app/src/main/java/com/example/viewmodel/AppViewModel.kt', 'r') as f:
    content = f.read()

new_delete = """    fun deleteDailyLog(id: String) {
        viewModelScope.launch {
            logRepository.deleteLogById(id)
            _notificationEvent.emit("Daily log deleted.")
        }
    }"""
content = content.replace('    fun addDailyLog(mood: Int, symptoms: String, notes: String) {', new_delete + '\n\n    fun addDailyLog(mood: Int, symptoms: String, notes: String) {')

with open('app/src/main/java/com/example/viewmodel/AppViewModel.kt', 'w') as f:
    f.write(content)

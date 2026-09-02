import re
with open('app/src/main/java/com/example/viewmodel/AppViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace("dailyLogs.forEach { logRepository.insertLog(it) }", "logRepository.insertLogs(dailyLogs)")

with open('app/src/main/java/com/example/viewmodel/AppViewModel.kt', 'w') as f:
    f.write(content)

with open("app/src/main/java/com/example/viewmodel/AppViewModel.kt", "r") as f:
    content = f.read()

old_1 = 'val logsStr = currentLogs.joinToString(";;;") { "${it.id}|${it.timestamp}|${it.mood}|${it.notes}|${it.energyLevel}" }'
new_1 = 'val logsStr = currentLogs.joinToString(";;;") { "${it.id}|${it.timestamp}|${it.mood}|${it.symptoms}|${it.notes}" }'
content = content.replace(old_1, new_1)

old_2 = 'com.example.data.model.DailyLog(parts[0], parts[1].toLong(), parts[2], parts[3], parts[4].toInt())'
new_2 = 'com.example.data.model.DailyLog(parts[0], parts[1].toLong(), parts[2].toInt(), parts[3], parts[4])'
content = content.replace(old_2, new_2)

with open("app/src/main/java/com/example/viewmodel/AppViewModel.kt", "w") as f:
    f.write(content)

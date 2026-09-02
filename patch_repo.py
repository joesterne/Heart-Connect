import re
with open('app/src/main/java/com/example/data/repository/LogRepository.kt', 'r') as f:
    content = f.read()

new_repo = """    suspend fun insertLog(log: DailyLog) = dailyLogDao.insertLog(log)
    suspend fun insertLogs(logs: List<DailyLog>) = dailyLogDao.insertLogs(logs)"""

content = content.replace("    suspend fun insertLog(log: DailyLog) = dailyLogDao.insertLog(log)", new_repo)

with open('app/src/main/java/com/example/data/repository/LogRepository.kt', 'w') as f:
    f.write(content)

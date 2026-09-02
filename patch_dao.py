import re
with open('app/src/main/java/com/example/data/database/DailyLogDao.kt', 'r') as f:
    content = f.read()

new_insert = """    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DailyLog)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<DailyLog>)"""

content = content.replace("    @Insert(onConflict = OnConflictStrategy.REPLACE)\n    suspend fun insertLog(log: DailyLog)", new_insert)

with open('app/src/main/java/com/example/data/database/DailyLogDao.kt', 'w') as f:
    f.write(content)

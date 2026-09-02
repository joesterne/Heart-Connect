import re

with open('app/src/main/java/com/example/data/model/Models.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'import androidx.compose.runtime.Immutable',
    'import androidx.compose.runtime.Immutable\nimport androidx.room.Entity\nimport androidx.room.PrimaryKey'
)

content = content.replace(
    '''@Immutable
data class DailyLog(
    val id: String = "",''',
    '''@Entity(tableName = "daily_logs")
@Immutable
data class DailyLog(
    @PrimaryKey val id: String = "",'''
)

with open('app/src/main/java/com/example/data/model/Models.kt', 'w') as f:
    f.write(content)

import re
with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'r') as f:
    content = f.read()

# Add missing imports if they don't exist
imports = [
    "import androidx.compose.ui.draw.clip",
    "import androidx.compose.foundation.clickable",
    "import androidx.compose.ui.unit.sp",
    "import androidx.compose.ui.graphics.Color"
]

for imp in imports:
    if imp not in content:
        content = content.replace('import androidx.compose.ui.unit.dp', f'import androidx.compose.ui.unit.dp\n{imp}')

old_mood = """                Text("Mood (1-5): ${mood.toInt()}", style = MaterialTheme.typography.bodyMedium)
                Slider(
                    value = mood,
                    onValueChange = { mood = it },
                    valueRange = 1f..5f,
                    steps = 3
                )"""

new_mood = """                Text("How are you feeling?", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val emojis = listOf(1 to "😞", 2 to "😕", 3 to "😐", 4 to "🙂", 5 to "😄")
                    emojis.forEach { (value, emoji) ->
                        val isSelected = mood.toInt() == value
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable { mood = value.toFloat() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 24.sp)
                        }
                    }
                }"""

content = content.replace(old_mood, new_mood)

old_mood_log = """Text("Mood: ${log.mood}/5", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)"""
new_mood_log = """val emoji = when (log.mood) { 1 -> "😞"; 2 -> "😕"; 3 -> "😐"; 4 -> "🙂"; 5 -> "😄"; else -> "😐" }
                            Text(emoji, style = MaterialTheme.typography.titleMedium)"""

content = content.replace(old_mood_log, new_mood_log)

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'w') as f:
    f.write(content)


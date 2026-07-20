with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "r") as f:
    content = f.read()

old_chart = """val thirtyDaysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
    val recentLogs = logs.filter { it.timestamp >= thirtyDaysAgo }.sortedBy { it.timestamp }

    if (recentLogs.isEmpty()) {
        Text("No mood data yet to display a 30-day trend.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }

    val averageMood = recentLogs.map { it.mood }.average()"""

new_chart = """val recentLogs = remember(logs) {
        val thirtyDaysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        logs.filter { it.timestamp >= thirtyDaysAgo }.sortedBy { it.timestamp }
    }

    if (recentLogs.isEmpty()) {
        Text("No mood data yet to display a 30-day trend.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }

    val averageMood = remember(recentLogs) { recentLogs.map { it.mood }.average() }"""

content = content.replace(old_chart, new_chart)

old_display = """val maxItems = 30
            val displayLogs = recentLogs.takeLast(maxItems)"""

new_display = """val displayLogs = remember(recentLogs) {
                val maxItems = 30
                recentLogs.takeLast(maxItems)
            }"""

content = content.replace(old_display, new_display)

with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "w") as f:
    f.write(content)

import re

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'r') as f:
    content = f.read()

old_code = """        val chartEntryModel = remember(recentLogs) {
            val maxItems = 30
            val displayLogs = recentLogs.takeLast(maxItems)
            val entries = displayLogs.mapIndexed { index, log -> 
                FloatEntry(x = index.toFloat(), y = log.mood.toFloat()) 
            }
            entryModelOf(*entries.toTypedArray())
        }
        
        if (chartEntryModel.entries.firstOrNull()?.isNotEmpty() == true) {
            Chart(
                chart = lineChart(),
                model = chartEntryModel,
                startAxis = rememberStartAxis(
                    valueFormatter = { value, _ -> value.toInt().toString() }
                ),
                bottomAxis = rememberBottomAxis(
                    valueFormatter = { _, _ -> "" }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
        }"""

new_code = """        val chartEntryModel = remember(recentLogs) {
            val maxItems = 30
            val displayLogs = recentLogs.takeLast(maxItems)
            if (displayLogs.isNotEmpty()) {
                val entries: List<FloatEntry> = displayLogs.mapIndexed { index, log -> 
                    FloatEntry(x = index.toFloat(), y = log.mood.toFloat()) 
                }
                entryModelOf(entries)
            } else {
                null
            }
        }
        
        if (chartEntryModel != null) {
            Chart(
                chart = lineChart(),
                model = chartEntryModel,
                startAxis = rememberStartAxis(
                    valueFormatter = { value, _ -> value.toInt().toString() }
                ),
                bottomAxis = rememberBottomAxis(
                    valueFormatter = { _, _ -> "" }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
        }"""

content = content.replace(old_code, new_code)

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'w') as f:
    f.write(content)


import re

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'r') as f:
    content = f.read()

vico_imports = """import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf"""

if "com.patrykandpatrick.vico" not in content:
    content = content.replace("import com.example.viewmodel.AppViewModel", "import com.example.viewmodel.AppViewModel\n" + vico_imports)

old_chart = """        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val displayLogs = remember(recentLogs) {
                val maxItems = 30
                recentLogs.takeLast(maxItems)
            }
            displayLogs.forEach { log ->
                val color = when(log.mood) {
                    5, 4 -> MaterialTheme.colorScheme.primary
                    3 -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.error
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .fillMaxHeight(log.mood / 5f)
                        .background(color, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                )
            }
        }"""

new_chart = """        val chartEntryModel = remember(recentLogs) {
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
        
content = content.replace(old_chart, new_chart)

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'w') as f:
    f.write(content)


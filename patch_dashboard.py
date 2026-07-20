with open("app/src/main/java/com/example/ui/screens/DashboardScreen.kt", "r") as f:
    content = f.read()

content = content.replace("onNavigateToFavorites: () -> Unit,", "onNavigateToFavorites: () -> Unit,\n    onNavigateToMetrics: () -> Unit,")

button = """
            item {
                Button(
                    onClick = onNavigateToMetrics,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.MonitorHeart, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Health Metrics Dashboard")
                }
            }"""

content = content.replace('item {\n                Button(\n                    onClick = onNavigateToFavorites', button + '\n            item {\n                Button(\n                    onClick = onNavigateToFavorites')

content = content.replace("import androidx.compose.material.icons.filled.MedicalServices", "import androidx.compose.material.icons.filled.MedicalServices\nimport androidx.compose.material.icons.filled.MonitorHeart")

with open("app/src/main/java/com/example/ui/screens/DashboardScreen.kt", "w") as f:
    f.write(content)

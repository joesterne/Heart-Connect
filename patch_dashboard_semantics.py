with open("app/src/main/java/com/example/ui/screens/DashboardScreen.kt", "r") as f:
    content = f.read()

import_semantics = "import androidx.compose.ui.semantics.*\nimport androidx.compose.ui.unit.dp"
if "import androidx.compose.ui.semantics.*" not in content:
    content = content.replace("import androidx.compose.ui.unit.dp", import_semantics)

old_profile_card = """@Composable
fun ProfileCard(profile: Profile, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),"""

new_profile_card = """@Composable
fun ProfileCard(profile: Profile, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {
            contentDescription = "Profile card for ${profile.name}, ${profile.age} years old in ${profile.location}. Medical history: ${profile.medicalHistory}."
        },"""

content = content.replace(old_profile_card, new_profile_card)

old_resource_item = """@Composable
fun ResourceContactItem(title: String, info: String, desc: String) {
    Column {"""

new_resource_item = """@Composable
fun ResourceContactItem(title: String, info: String, desc: String) {
    Column(modifier = Modifier.semantics(mergeDescendants = true) {
        contentDescription = "$title, contact: $info. $desc"
    }) {"""

content = content.replace(old_resource_item, new_resource_item)

# Let's also add the High Contrast Toggle to the Dashboard
toggle_card = """            item {
                val isHighContrast by viewModel.isHighContrast.collectAsState()
                Card(
                    modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {
                        contentDescription = "Accessibility options"
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("High Contrast Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Improves readability for visual accessibility", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = isHighContrast,
                            onCheckedChange = { viewModel.toggleHighContrast(it) },
                            modifier = Modifier.semantics { contentDescription = "Toggle high contrast mode" }
                        )
                    }
                }
            }"""

if "High Contrast Mode" not in content:
    content = content.replace("userProfile?.let { myProfile ->", toggle_card + "\n\n            userProfile?.let { myProfile ->")


with open("app/src/main/java/com/example/ui/screens/DashboardScreen.kt", "w") as f:
    f.write(content)

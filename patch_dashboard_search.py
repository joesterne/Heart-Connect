with open("app/src/main/java/com/example/ui/screens/DashboardScreen.kt", "r") as f:
    content = f.read()

old_search = '''                    trailingIcon = {
                        IconButton(onClick = { viewModel.searchMedicalInfo(searchQuery) }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }'''

new_search = '''                    trailingIcon = {
                        val context = androidx.compose.ui.platform.LocalContext.current
                        IconButton(onClick = { 
                            if (searchQuery.trim().length < 3) {
                                android.widget.Toast.makeText(context, "Search query must be at least 3 characters.", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.searchMedicalInfo(searchQuery.trim()) 
                            }
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }'''

content = content.replace(old_search, new_search)

with open("app/src/main/java/com/example/ui/screens/DashboardScreen.kt", "w") as f:
    f.write(content)

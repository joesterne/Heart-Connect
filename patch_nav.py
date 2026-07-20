with open("app/src/main/java/com/example/ui/navigation/AppNavigation.kt", "r") as f:
    content = f.read()

content = content.replace("import com.example.ui.screens.ProfileScreen", "import com.example.ui.screens.ProfileScreen\nimport com.example.ui.screens.MetricsScreen")
content = content.replace("import androidx.compose.material.icons.filled.Favorite", "import androidx.compose.material.icons.filled.Favorite\nimport androidx.compose.material.icons.filled.MonitorHeart")

drawer_item = """
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.MonitorHeart, contentDescription = null) },
                        label = { Text("Health Metrics") },
                        selected = currentRoute == "metrics",
                        onClick = { scope.launch { drawerState.close() }; navController.navigate("metrics") { popUpTo(0) } },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )"""

content = content.replace('onClick = { scope.launch { drawerState.close() }; navController.navigate("favorites") { popUpTo(0) } },\n                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)\n                    )', 'onClick = { scope.launch { drawerState.close() }; navController.navigate("favorites") { popUpTo(0) } },\n                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)\n                    )' + drawer_item)

composable = """
                composable("metrics") {
                    MetricsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }"""

content = content.replace('composable("favorites") {', composable + '\n                composable("favorites") {')

# Also add to dashboard route
content = content.replace('onNavigateToFavorites = { navController.navigate("favorites") },', 'onNavigateToFavorites = { navController.navigate("favorites") },\n                        onNavigateToMetrics = { navController.navigate("metrics") },')

with open("app/src/main/java/com/example/ui/navigation/AppNavigation.kt", "w") as f:
    f.write(content)

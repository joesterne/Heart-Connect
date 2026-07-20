for file in app/src/main/java/com/example/ui/screens/*.kt; do
    if [[ "$file" != *"AuthScreen.kt"* && "$file" != *"DashboardScreen.kt"* ]]; then
        perl -0777 -pi -e 's/navigationIcon = \{\s*IconButton\(onClick = onBack\) \{\s*Icon\(Icons\.AutoMirrored\.Filled\.ArrowBack, contentDescription = "Back"\)\s*\}\s*\}/navigationIcon = {\n                    val openDrawer = com.example.ui.navigation.LocalOpenDrawer.current\n                    IconButton(onClick = openDrawer) {\n                        Icon(androidx.compose.material.icons.Icons.Default.Menu, contentDescription = "Menu")\n                    }\n                }/g' "$file"
    fi
done

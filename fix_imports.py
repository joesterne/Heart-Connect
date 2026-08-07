with open("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.material.icons.filled.Shield\\nimport androidx.compose.material.icons.filled.Check\\nimport androidx.compose.material.icons.filled.Event", "import androidx.compose.material.icons.filled.Shield\nimport androidx.compose.material.icons.filled.Check\nimport androidx.compose.material.icons.filled.Event")

with open("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", "w") as f:
    f.write(content)

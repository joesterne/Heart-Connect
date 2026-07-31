with open("app/src/main/java/com/example/viewmodel/AppViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("import kotlinx.coroutines.launch\\nimport kotlinx.coroutines.async", "import kotlinx.coroutines.launch\nimport kotlinx.coroutines.async")

with open("app/src/main/java/com/example/viewmodel/AppViewModel.kt", "w") as f:
    f.write(content)

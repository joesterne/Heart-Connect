with open("app/src/main/java/com/example/viewmodel/AppViewModel.kt", "r") as f:
    content = f.read()

if "import kotlinx.coroutines.async" not in content:
    content = content.replace("import kotlinx.coroutines.launch", "import kotlinx.coroutines.launch\\nimport kotlinx.coroutines.async")

old_async = """                val profilesDeferred = kotlinx.coroutines.async { firestoreRepository.getProfiles() }
                val groupsDeferred = kotlinx.coroutines.async { firestoreRepository.getGroups() }
                val postsDeferred = kotlinx.coroutines.async { firestoreRepository.getPosts() }"""

new_async = """                val profilesDeferred = async { firestoreRepository.getProfiles() }
                val groupsDeferred = async { firestoreRepository.getGroups() }
                val postsDeferred = async { firestoreRepository.getPosts() }"""

content = content.replace(old_async, new_async)

with open("app/src/main/java/com/example/viewmodel/AppViewModel.kt", "w") as f:
    f.write(content)

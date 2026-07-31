with open("app/src/main/java/com/example/viewmodel/AppViewModel.kt", "r") as f:
    content = f.read()

old_loading = """        viewModelScope.launch {
            _isGlobalLoading.value = true
            try {
                _profiles.value = firestoreRepository.getProfiles()
                _supportGroups.value = firestoreRepository.getGroups()
                _communityPosts.value = firestoreRepository.getPosts()
            } catch (e: Exception) {"""

new_loading = """        viewModelScope.launch {
            _isGlobalLoading.value = true
            try {
                val profilesDeferred = kotlinx.coroutines.async { firestoreRepository.getProfiles() }
                val groupsDeferred = kotlinx.coroutines.async { firestoreRepository.getGroups() }
                val postsDeferred = kotlinx.coroutines.async { firestoreRepository.getPosts() }
                
                _profiles.value = profilesDeferred.await()
                _supportGroups.value = groupsDeferred.await()
                _communityPosts.value = postsDeferred.await()
            } catch (e: Exception) {"""

content = content.replace(old_loading, new_loading)

with open("app/src/main/java/com/example/viewmodel/AppViewModel.kt", "w") as f:
    f.write(content)

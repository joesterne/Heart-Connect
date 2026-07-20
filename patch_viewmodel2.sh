sed -i '/val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()/a \
    val recommendedMentors: StateFlow<List<Profile>> = combine(_userProfile, _profiles) { user, allProfiles ->\
        if (user == null || user.isAvailableForMentorship) return@combine emptyList()\
        val userKeywords = user.medicalHistory.split(Regex("\\\\W+")).map { it.lowercase() }.filter { it.length > 3 }.toSet()\
        allProfiles.filter { it.isAvailableForMentorship && it.id != user.id }.sortedByDescending { mentor ->\
            val mentorKeywords = mentor.medicalHistory.split(Regex("\\\\W+")).map { it.lowercase() }.filter { it.length > 3 }.toSet()\
            userKeywords.intersect(mentorKeywords).size\
        }.take(3)\
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())' app/src/main/java/com/example/viewmodel/AppViewModel.kt

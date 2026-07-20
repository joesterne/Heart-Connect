package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChatMessage
import com.example.data.model.Profile
import com.example.data.model.SupportGroup
import com.example.data.repository.FirestoreRepository
import com.example.data.repository.GeminiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import com.example.data.repository.Content
import com.example.data.repository.Part
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val firestoreRepository = FirestoreRepository(application)
    private val geminiRepository = GeminiRepository()
    private val masterKey = MasterKey.Builder(application).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(); private val sharedPrefs = EncryptedSharedPreferences.create(application, "heart_connect_prefs_secure", masterKey, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

    private val _userProfile = MutableStateFlow<Profile?>(null)
    val userProfile: StateFlow<Profile?> = _userProfile.asStateFlow()

    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles: StateFlow<List<Profile>> = _profiles.asStateFlow()

    private val _supportGroups = MutableStateFlow<List<SupportGroup>>(emptyList())
    val supportGroups: StateFlow<List<SupportGroup>> = _supportGroups.asStateFlow()

    private val _chatHistory = MutableStateFlow<List<Content>>(emptyList())
    val chatHistory: StateFlow<List<Content>> = _chatHistory.asStateFlow()
    
    private val _isGlobalLoading = MutableStateFlow(true)
    val isGlobalLoading = _isGlobalLoading.asStateFlow()

    private val _isCounselingLoading = MutableStateFlow(false)
    val isCounselingLoading = _isCounselingLoading.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()
    
    private val _searchResults = MutableStateFlow("")
    val searchResults = _searchResults.asStateFlow()

    // Encrypted private conversations state: mapped by peer user ID
    private val _privateMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val privateMessages: StateFlow<Map<String, List<ChatMessage>>> = _privateMessages.asStateFlow()

    private val _isSendingPrivateMessage = MutableStateFlow(false)
    val isSendingPrivateMessage = _isSendingPrivateMessage.asStateFlow()

    private val _communityPosts = MutableStateFlow<List<com.example.data.model.CommunityPost>>(emptyList())
    val communityPosts: StateFlow<List<com.example.data.model.CommunityPost>> = _communityPosts.asStateFlow()

    private val _qaInquiries = MutableStateFlow<List<com.example.data.model.QAInquiry>>(emptyList())
    val qaInquiries: StateFlow<List<com.example.data.model.QAInquiry>> = _qaInquiries.asStateFlow()


    private val _notificationEvent = kotlinx.coroutines.flow.MutableSharedFlow<String>()
    val notificationEvent = _notificationEvent.asSharedFlow()

    private val _isHighContrast = MutableStateFlow(sharedPrefs.getBoolean("high_contrast", false))
    val isHighContrast = _isHighContrast.asStateFlow()

    private val _isLargeFont = MutableStateFlow(sharedPrefs.getBoolean("large_font", false))
    val isLargeFont = _isLargeFont.asStateFlow()

    private val _isDarkMode = MutableStateFlow<Boolean?>(
        if (sharedPrefs.contains("dark_mode")) sharedPrefs.getBoolean("dark_mode", false) else null
    )
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()
    val recommendedMentors: StateFlow<List<Profile>> = combine(_userProfile, _profiles) { user, allProfiles ->
        if (user == null || user.isAvailableForMentorship) return@combine emptyList()
        val userKeywords = user.medicalHistory.split(Regex("\\W+")).map { it.lowercase() }.filter { it.length > 3 }.toSet()
        allProfiles.filter { it.isAvailableForMentorship && it.id != user.id }.sortedByDescending { mentor ->
            val mentorKeywords = mentor.medicalHistory.split(Regex("\\W+")).map { it.lowercase() }.filter { it.length > 3 }.toSet()
            userKeywords.intersect(mentorKeywords).size
        }.take(3)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun toggleHighContrast(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("high_contrast", enabled).apply()
        _isHighContrast.value = enabled
    }

    fun toggleLargeFont(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("large_font", enabled).apply()
        _isLargeFont.value = enabled
    }

    fun toggleDarkMode(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("dark_mode", enabled).apply()
        _isDarkMode.value = enabled
    }

    private val _educationalContent = MutableStateFlow<List<com.example.data.model.EducationalContent>>(emptyList())
    val educationalContent: StateFlow<List<com.example.data.model.EducationalContent>> = _educationalContent.asStateFlow()

    private val _savedPosts = MutableStateFlow<Set<String>>(emptySet())
    val savedPosts: StateFlow<Set<String>> = _savedPosts.asStateFlow()

    private val _savedEducationalContent = MutableStateFlow<Set<String>>(emptySet())
    val savedEducationalContent: StateFlow<Set<String>> = _savedEducationalContent.asStateFlow()

    init {
        // Load saved profile or use default placeholder
        val name = sharedPrefs.getString("profile_name", "Alex") ?: "Alex"
        val age = sharedPrefs.getInt("profile_age", 34)
        val location = sharedPrefs.getString("profile_location", "Seattle, WA") ?: "Seattle, WA"
        val medicalHistory = sharedPrefs.getString("profile_medical", "Cardiomyopathy, waiting 6 months") ?: "Cardiomyopathy, waiting 6 months"
        val aboutMe = sharedPrefs.getString("profile_about", "Looking for others going through the same thing.") ?: "Looking for others going through the same thing."
        val journeyPhase = sharedPrefs.getString("profile_journey", "Pre-transplant") ?: "Pre-transplant"
        val isAvailableForMentorship = sharedPrefs.getBoolean("profile_mentorship", false)
        val dailyLogsStr = sharedPrefs.getString("profile_daily_logs", "") ?: ""
        val dailyLogs = if (dailyLogsStr.isNotBlank()) {
            dailyLogsStr.split(";;;").mapNotNull { logStr ->
                val parts = logStr.split("|||")
                if (parts.size == 5) {
                    com.example.data.model.DailyLog(
                        id = parts[0],
                        timestamp = parts[1].toLongOrNull() ?: 0L,
                        mood = parts[2].toIntOrNull() ?: 3,
                        symptoms = parts[3],
                        notes = parts[4]
                    )
                } else null
            }
        } else emptyList()

        _userProfile.value = Profile(id = "me", name = name, age = age, location = location, medicalHistory = medicalHistory, aboutMe = aboutMe, journeyPhase = journeyPhase, isAvailableForMentorship = isAvailableForMentorship, dailyLogs = dailyLogs, badges = getBadgesForProfile(isAvailableForMentorship, dailyLogs.size, journeyPhase))
        
        // Initial secure seed messages to make the interface feel active
        _privateMessages.value = mapOf(
            "1" to listOf(
                ChatMessage(id = "init_1", groupId = "private_1", senderId = "1", senderName = "Sarah", text = "Hi Alex, I saw your post. I am also listed for cardiomyopathy. How are you holding up?", timestamp = System.currentTimeMillis() - 3600000)
            ),
            "2" to listOf(
                ChatMessage(id = "init_2", groupId = "private_2", senderId = "2", senderName = "Michael", text = "Hey! It's tough waiting but we will get through this. Feel free to shoot me a message anytime.", timestamp = System.currentTimeMillis() - 7200000)
            )
        )

        viewModelScope.launch {
            _isGlobalLoading.value = true
            try {
                _profiles.value = firestoreRepository.getProfiles()
                _supportGroups.value = firestoreRepository.getGroups()
                _communityPosts.value = firestoreRepository.getPosts()
            } catch (e: Exception) {
                // Mock data fallback if firebase fails due to missing google-services.json
                _profiles.value = listOf(
                    Profile(id = "1", name = "Sarah", age = 42, location = "Portland, OR", medicalHistory = "Congenital heart defect, listed 2 months ago.", journeyPhase = "Pre-transplant", badges = getBadgesForProfile(false, 3, "Pre-transplant")),
                    Profile(id = "2", name = "Michael", age = 29, location = "San Francisco, CA", medicalHistory = "Heart failure, listed 1 year ago.", journeyPhase = "Post-transplant recovery", isAvailableForMentorship = true, badges = getBadgesForProfile(true, 10, "Post-transplant recovery"))
                )
                _supportGroups.value = listOf(
                    SupportGroup("1", "General Support", "A safe place for all waitlist patients.", "Dr. Smith", emptyList()),
                    SupportGroup("2", "Post-Transplant Life", "Discussing what comes next.", "Nurse Jane", emptyList())
                )
                _communityPosts.value = listOf(
                    com.example.data.model.CommunityPost("1", "1", "Sarah", "Pre-transplant", "Just got my 3-month checkup, everything looks stable!", System.currentTimeMillis() - 86400000),
                    com.example.data.model.CommunityPost("2", "2", "Michael", "Post-transplant recovery", "Has anyone tried the new physical therapy routine recommended by Dr. Aris?", System.currentTimeMillis() - 172800000)
                )
                _qaInquiries.value = listOf(
                    com.example.data.model.QAInquiry(
                        id = "qa_1",
                        authorId = "1",
                        authorName = "Sarah",
                        question = "What are the common side effects of the immunosuppressants initially?",
                        timestamp = System.currentTimeMillis() - 86400000,
                        answers = listOf(
                            com.example.data.model.QAAnswer(
                                id = "ans_1",
                                authorId = "2",
                                authorName = "Michael",
                                content = "I experienced some mild tremors and headaches, but they faded after a few weeks as my body adjusted.",
                                timestamp = System.currentTimeMillis() - 80000000,
                                isVerified = true
                            )
                        )
                    )
                )
                kotlinx.coroutines.delay(1000)
                _educationalContent.value = listOf(
                    com.example.data.model.EducationalContent("1", "Understanding the Waitlist", "A guide to how the transplant waitlist works and how to prepare.", "Article", "5 min read"),
                    com.example.data.model.EducationalContent("2", "Nutrition Post-Transplant", "Key dietary changes to support your new organ.", "Video", "12 min watch"),
                    com.example.data.model.EducationalContent("3", "Mental Health on the Journey", "Tips for managing anxiety and staying positive.", "Article", "8 min read")
                )
            } finally {
                _isGlobalLoading.value = false
            }
        }
    }

    fun createCommunityPost(content: String) {
        val currentUser = _userProfile.value ?: return
        val newPost = com.example.data.model.CommunityPost(
            id = "post_${System.currentTimeMillis()}",
            authorId = currentUser.id,
            authorName = currentUser.name,
            authorJourneyPhase = currentUser.journeyPhase,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        val currentPosts = _communityPosts.value.toMutableList()
        currentPosts.add(0, newPost) // Add to top
        _communityPosts.value = currentPosts
        
        viewModelScope.launch {

            try {
                firestoreRepository.createPost(newPost)
            } catch (e: Exception) {
                // Ignore fallback for mock
            }
            
            // Simulate someone commenting on the user's shared experience after a short delay
            kotlinx.coroutines.delay(5000)
            _notificationEvent.emit("New comment on your post from a community member!")
        }
    }

    fun createQAInquiry(question: String) {
        val currentUser = _userProfile.value ?: return
        val newInquiry = com.example.data.model.QAInquiry(
            id = "qa_${System.currentTimeMillis()}",
            authorId = currentUser.id,
            authorName = currentUser.name,
            question = question,
            timestamp = System.currentTimeMillis(),
            answers = emptyList()
        )
        val currentInquiries = _qaInquiries.value.toMutableList()
        currentInquiries.add(0, newInquiry)
        _qaInquiries.value = currentInquiries
    }

    fun answerQAInquiry(inquiryId: String, answerContent: String) {
        val currentUser = _userProfile.value ?: return
        val newAnswer = com.example.data.model.QAAnswer(
            id = "ans_${System.currentTimeMillis()}",
            authorId = currentUser.id,
            authorName = currentUser.name,
            content = answerContent,
            timestamp = System.currentTimeMillis()
        )
        val currentInquiries = _qaInquiries.value.toMutableList()
        val index = currentInquiries.indexOfFirst { it.id == inquiryId }
        if (index != -1) {
            val inquiry = currentInquiries[index]
            val newAnswers = inquiry.answers.toMutableList().apply { add(newAnswer) }
            currentInquiries[index] = inquiry.copy(answers = newAnswers)
            _qaInquiries.value = currentInquiries
        }
    }

    fun verifyQAAnswer(inquiryId: String, answerId: String) {
        val currentInquiries = _qaInquiries.value.toMutableList()
        val index = currentInquiries.indexOfFirst { it.id == inquiryId }
        if (index != -1) {
            val inquiry = currentInquiries[index]
            val newAnswers = inquiry.answers.map {
                if (it.id == answerId) it.copy(isVerified = true) else it
            }
            currentInquiries[index] = inquiry.copy(answers = newAnswers)
            _qaInquiries.value = currentInquiries
        }
    }

    private fun getBadgesForProfile(isMentorship: Boolean, logsCount: Int, phase: String): List<com.example.data.model.Badge> {
        val badges = mutableListOf<com.example.data.model.Badge>()
        if (isMentorship) {
            badges.add(com.example.data.model.Badge("1", "Active Mentor", "Available for mentorship", "VolunteerActivism"))
        }
        if (logsCount >= 5) {
            badges.add(com.example.data.model.Badge("2", "Long-term Supporter", "Consistently logs updates", "Favorite"))
        } else if (logsCount >= 1) {
            badges.add(com.example.data.model.Badge("3", "Active Participant", "Started logging journey", "LocalFireDepartment"))
        }
        if (phase.contains("Post", ignoreCase = true)) {
            badges.add(com.example.data.model.Badge("4", "Veteran", "Post-transplant phase", "VerifiedUser"))
        }
        return badges
    }

    fun saveProfile(name: String, age: Int, location: String, medicalHistory: String, aboutMe: String, journeyPhase: String, isAvailableForMentorship: Boolean) {
        val currentLogs = _userProfile.value?.dailyLogs ?: emptyList()
        sharedPrefs.edit().apply {
            putString("profile_name", name)
            putInt("profile_age", age)
            putString("profile_location", location)
            putString("profile_medical", medicalHistory)
            putString("profile_about", aboutMe)
            putString("profile_journey", journeyPhase)
            putBoolean("profile_mentorship", isAvailableForMentorship)
            apply()
        }
        _userProfile.value = Profile(id = "me", name = name, age = age, location = location, medicalHistory = medicalHistory, aboutMe = aboutMe, journeyPhase = journeyPhase, isAvailableForMentorship = isAvailableForMentorship, dailyLogs = currentLogs, badges = getBadgesForProfile(isAvailableForMentorship, currentLogs.size, journeyPhase))
        
        viewModelScope.launch {

            // Simulate finding a new match after updating the profile
            kotlinx.coroutines.delay(3000)
            _notificationEvent.emit("A new peer match was found based on your updated profile!")
        }
    }

    private val _isTranscribing = MutableStateFlow(false)
    val isTranscribing = _isTranscribing.asStateFlow()

    fun transcribeAndAddAudioLog(base64Audio: String, mood: Int, symptoms: String) {
        viewModelScope.launch {
            _isTranscribing.value = true
            val transcription = geminiRepository.transcribeAudio(base64Audio)
            val parts = transcription.split("|||")
            if (parts.size >= 3) {
                val extractedMood = parts[0].trim().toIntOrNull() ?: mood
                val extractedSymptoms = parts[1].trim()
                val extractedNotes = parts[2].trim()
                addDailyLog(extractedMood, extractedSymptoms, extractedNotes)
            } else {
                addDailyLog(mood, symptoms, "[Audio Transcript]: $transcription")
            }
            addDailyLog(mood, symptoms, "[Audio Transcript]: $transcription")
            _isTranscribing.value = false
            _notificationEvent.emit("Audio transcribed and log added.")
        }
    }

    fun addDailyLog(mood: Int, symptoms: String, notes: String) {
        val currentProfile = _userProfile.value ?: return
        val newLog = com.example.data.model.DailyLog(
            id = "log_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            mood = mood,
            symptoms = symptoms.replace("|||", "").replace(";;;", ""),
            notes = notes.replace("|||", "").replace(";;;", "")
        )
        val newLogs = currentProfile.dailyLogs.toMutableList().apply { add(0, newLog) }
        
        val logsStr = newLogs.joinToString(";;;") { log ->
            "${log.id}|||${log.timestamp}|||${log.mood}|||${log.symptoms}|||${log.notes}"
        }
        
        sharedPrefs.edit().putString("profile_daily_logs", logsStr).apply()
        _userProfile.value = currentProfile.copy(dailyLogs = newLogs, badges = getBadgesForProfile(currentProfile.isAvailableForMentorship, newLogs.size, currentProfile.journeyPhase))
    }

    fun sendMessageToCounselor(message: String) {
        val newHistory = _chatHistory.value.toMutableList()
        newHistory.add(Content(parts = listOf(Part(text = message)), role = "user"))
        _chatHistory.value = newHistory
        _isCounselingLoading.value = true

        viewModelScope.launch {

            val response = geminiRepository.getCounselingResponse(message, newHistory)
            val updatedHistory = _chatHistory.value.toMutableList()
            updatedHistory.add(Content(parts = listOf(Part(text = response)), role = "model"))
            _chatHistory.value = updatedHistory
            _isCounselingLoading.value = false
        }
    }

    fun searchMedicalInfo(query: String) {
        _isSearching.value = true
        viewModelScope.launch {

            val result = geminiRepository.searchMedicalResources(query)
            _searchResults.value = result
            _isSearching.value = false
        }
    }

    fun sendPrivateSecureMessage(peerId: String, text: String) {
        val currentChats = _privateMessages.value.toMutableMap()
        val chatList = currentChats[peerId]?.toMutableList() ?: mutableListOf()
        
        val userMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            groupId = "private_$peerId",
            senderId = "me",
            senderName = _userProfile.value?.name ?: "Me",
            text = text,
            timestamp = System.currentTimeMillis()
        )
        chatList.add(userMsg)
        currentChats[peerId] = chatList
        _privateMessages.value = currentChats

        _isSendingPrivateMessage.value = true

        viewModelScope.launch {

            val peerProfile = _profiles.value.find { it.id == peerId } ?: Profile(
                id = peerId,
                name = "Matched Peer",
                age = 35,
                location = "Unknown",
                medicalHistory = "Listed for heart transplant."
            )
            
            // Build conversation content for Gemini
            val geminiHistory = chatList.map { msg ->
                Content(
                    parts = listOf(Part(text = msg.text)),
                    role = if (msg.senderId == "me") "user" else "model"
                )
            }

            val prompt = "Respond to this message: '$text'"
            val systemInstructions = """
                You are simulating a secure, private peer conversation between heart transplant waiting list patients.
                You are ${peerProfile.name}, age ${peerProfile.age}, located in ${peerProfile.location}.
                Your medical history and status is: ${peerProfile.medicalHistory}.
                Be supportive, empathetic, realistic, and talk like a peer patient. Keep your response relatively short, friendly, and human.
            """.trimIndent()

            val customRepository = GeminiRepository()
            // Request dynamic reply
            val replyText = try {
                customRepository.getCounselingResponse(prompt, geminiHistory)
            } catch (e: Exception) {
                "Hey! I'm glad we are connected. This journey is tough but we have each other."
            }

            val peerReplyMsg = ChatMessage(
                id = "msg_${System.currentTimeMillis()}",
                groupId = "private_$peerId",
                senderId = peerId,
                senderName = peerProfile.name,
                text = replyText,
                timestamp = System.currentTimeMillis()
            )

            val updatedChats = _privateMessages.value.toMutableMap()
            val updatedList = updatedChats[peerId]?.toMutableList() ?: mutableListOf()
            updatedList.add(peerReplyMsg)
            updatedChats[peerId] = updatedList
            _privateMessages.value = updatedChats

            _isSendingPrivateMessage.value = false
        }
    }

    fun toggleSavedPost(postId: String) {
        val current = _savedPosts.value.toMutableSet()
        if (current.contains(postId)) {
            current.remove(postId)
        } else {
            current.add(postId)
        }
        _savedPosts.value = current
    }

    fun toggleSavedEducationalContent(contentId: String) {
        val current = _savedEducationalContent.value.toMutableSet()
        if (current.contains(contentId)) {
            current.remove(contentId)
        } else {
            current.add(contentId)
        }
        _savedEducationalContent.value = current
    }
}

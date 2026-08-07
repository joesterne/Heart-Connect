package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChatMessage
import com.example.data.model.Profile
import com.example.data.model.SupportGroup
import com.example.data.repository.SecureStorageRepository
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
import kotlinx.coroutines.async
import com.example.data.repository.Content
import com.example.data.repository.Part
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val firestoreRepository = FirestoreRepository(application)
    private val secureStorageRepository = SecureStorageRepository(application)
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

    // Support group messages state: mapped by groupId
    private val _groupMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(
        mapOf(
            "1" to listOf(
                ChatMessage(
                    id = "g_msg_1",
                    groupId = "1",
                    senderId = "mod_1",
                    senderName = "Dr. Aris Thorne, MD",
                    isAnonymous = false,
                    isModerator = true,
                    moderatorBadge = "Cardiothoracic Specialist",
                    text = "Welcome to the Pre-Transplant Waitlist Circle. This safe space is monitored to ensure emotional support and medical safety. Please feel free to participate anonymously or with your display name.",
                    timestamp = System.currentTimeMillis() - 86400000
                ),
                ChatMessage(
                    id = "g_msg_2",
                    groupId = "1",
                    senderId = "anon_849",
                    senderName = "HopefulHeart_42",
                    isAnonymous = true,
                    anonymousAlias = "HopefulHeart_42",
                    isModerator = false,
                    text = "Lately the waiting is getting harder, especially with sleep disruption. How do others handle the late-night anxiety?",
                    timestamp = System.currentTimeMillis() - 43200000
                )
            ),
            "2" to listOf(
                ChatMessage(
                    id = "g_msg_3",
                    groupId = "2",
                    senderId = "mod_2",
                    senderName = "Nurse Sarah Jenkins, RN",
                    isAnonymous = false,
                    isModerator = true,
                    moderatorBadge = "Transplant Care Coordinator",
                    text = "Welcome everyone! Reminder for post-transplant members: always track temperature and pill schedules twice daily.",
                    timestamp = System.currentTimeMillis() - 172800000
                )
            ),
            "3" to listOf(
                ChatMessage(
                    id = "g_msg_4",
                    groupId = "3",
                    senderId = "mod_3",
                    senderName = "Elena Rostova, LCSW",
                    isAnonymous = false,
                    isModerator = true,
                    moderatorBadge = "Clinical Social Worker",
                    text = "Caregiver burnout is real and valid. Welcome to our supportive circle.",
                    timestamp = System.currentTimeMillis() - 259200000
                )
            )
        )
    )
    val groupMessages: StateFlow<Map<String, List<ChatMessage>>> = _groupMessages.asStateFlow()

    private val _isSendingGroupMessage = MutableStateFlow(false)
    val isSendingGroupMessage = _isSendingGroupMessage.asStateFlow()

    private val _isSendingPrivateMessage = MutableStateFlow(false)
    val isSendingPrivateMessage = _isSendingPrivateMessage.asStateFlow()

    private val _communityPosts = MutableStateFlow<List<com.example.data.model.CommunityPost>>(emptyList())
    val communityPosts: StateFlow<List<com.example.data.model.CommunityPost>> = _communityPosts.asStateFlow()

    private val _scheduledSessions = MutableStateFlow<List<com.example.data.model.ScheduledSession>>(
        listOf(
            com.example.data.model.ScheduledSession(
                id = "s1",
                groupId = "1",
                title = "Managing Waitlist Anxiety",
                moderatorName = "Dr. Aris Thorne, MD",
                startTime = System.currentTimeMillis() + 86400000,
                durationMinutes = 60,
                description = "Open discussion on coping strategies for pre-transplant stress.",
                attendeesCount = 12
            ),
            com.example.data.model.ScheduledSession(
                id = "s2",
                groupId = "2",
                title = "Post-Transplant Meds Check-in",
                moderatorName = "Nurse Sarah Jenkins, RN",
                startTime = System.currentTimeMillis() + 172800000,
                durationMinutes = 45,
                description = "Q&A regarding immunosuppressants and managing side effects.",
                attendeesCount = 8
            )
        )
    )
    val scheduledSessions: StateFlow<List<com.example.data.model.ScheduledSession>> = _scheduledSessions.asStateFlow()

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
                val profilesDeferred = async { firestoreRepository.getProfiles() }
                val groupsDeferred = async { firestoreRepository.getGroups() }
                val postsDeferred = async { firestoreRepository.getPosts() }
                
                _profiles.value = profilesDeferred.await()
                _supportGroups.value = groupsDeferred.await()
                _communityPosts.value = postsDeferred.await()
            } catch (e: Exception) {
                // Mock data fallback if firebase fails due to missing google-services.json
                _profiles.value = listOf(
                    Profile(id = "1", name = "Sarah", age = 42, location = "Portland, OR", medicalHistory = "Congenital heart defect, listed 2 months ago.", journeyPhase = "Pre-transplant", badges = getBadgesForProfile(false, 3, "Pre-transplant")),
                    Profile(id = "2", name = "Michael", age = 29, location = "San Francisco, CA", medicalHistory = "Heart failure, listed 1 year ago.", journeyPhase = "Post-transplant recovery", isAvailableForMentorship = true, badges = getBadgesForProfile(true, 10, "Post-transplant recovery"))
                )
                _supportGroups.value = listOf(
                    SupportGroup(
                        id = "1",
                        name = "Pre-Transplant Waitlist Circle",
                        description = "A compassionate, moderated space for patients navigating waitlist anxiety, physical limitations, and care coordination.",
                        moderatedBy = "Dr. Aris Thorne, MD",
                        moderatorTitle = "Cardiothoracic Transplant Specialist",
                        category = "Waitlist & Coping",
                        isAnonymousByDefault = true,
                        rules = listOf("Respect patient privacy", "Anonymous postings welcome", "No medical prescribing - peer emotional support only"),
                        members = listOf("1", "2")
                    ),
                    SupportGroup(
                        id = "2",
                        name = "Post-Transplant Recovery & Wellness",
                        description = "Professional guidance and community sharing on immunosuppressant management, organ rejection monitoring, and emotional readjustment.",
                        moderatedBy = "Nurse Sarah Jenkins, RN, BSN",
                        moderatorTitle = "Lead Transplant Care Coordinator",
                        category = "Post-Transplant Recovery",
                        isAnonymousByDefault = true,
                        rules = listOf("Verify medical facts with clinicians", "Safe supportive dialogue", "Zero tolerance for harassment"),
                        members = listOf("2")
                    ),
                    SupportGroup(
                        id = "3",
                        name = "Caregivers & Loved Ones Support",
                        description = "A safe haven for family members and caregivers managing emotional strain and logistical duties alongside their loved ones.",
                        moderatedBy = "Elena Rostova, LCSW",
                        moderatorTitle = "Clinical Social Worker & Family Counselor",
                        category = "Caregiver Support",
                        isAnonymousByDefault = true,
                        rules = listOf("Confidential peer support", "Clinical guidance available", "Be respectful"),
                        members = listOf("1")
                    )
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

        fun backupLogsSecurely() {
        val currentLogs = _userProfile.value?.dailyLogs ?: emptyList()
        val logsStr = currentLogs.joinToString(";;;") { "${it.id}|||${it.timestamp}|||${it.mood}|||${it.symptoms}|||${it.notes}" }
        secureStorageRepository.saveEncryptedFile("daily_logs_backup.enc", logsStr)
    }

    fun restoreLogsSecurely() {
        val logsStr = secureStorageRepository.readEncryptedFile("daily_logs_backup.enc")
        if (logsStr != null && logsStr.isNotBlank()) {
            val dailyLogs = logsStr.split(";;;").mapNotNull { logStr ->
                val parts = logStr.split("|||")
                if (parts.size == 5) {
                    com.example.data.model.DailyLog(parts[0], parts[1].toLong(), parts[2].toInt(), parts[3], parts[4])
                } else null
            }
            _userProfile.value = _userProfile.value?.copy(dailyLogs = dailyLogs)
            sharedPrefs.edit().putString("profile_daily_logs", logsStr).apply()
        }
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

            // Request dynamic reply
            val replyText = try {
                geminiRepository.getCounselingResponse(prompt, geminiHistory, systemInstructions)
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

    fun sendGroupMessage(groupId: String, text: String, isAnonymous: Boolean, customAlias: String = "") {
        val group = _supportGroups.value.find { it.id == groupId }
        val currentProfile = _userProfile.value
        val actualSenderName = currentProfile?.name ?: "Patient"
        val displayName = if (isAnonymous) (if (customAlias.isNotBlank()) customAlias else "Anonymous Member") else actualSenderName

        val userMessage = ChatMessage(
            id = "g_msg_${System.currentTimeMillis()}",
            groupId = groupId,
            senderId = currentProfile?.id ?: "me",
            senderName = actualSenderName,
            isAnonymous = isAnonymous,
            anonymousAlias = if (isAnonymous) displayName else "",
            isModerator = false,
            text = text,
            timestamp = System.currentTimeMillis()
        )

        val currentMap = _groupMessages.value.toMutableMap()
        val list = currentMap[groupId]?.toMutableList() ?: mutableListOf()
        list.add(userMessage)
        currentMap[groupId] = list
        _groupMessages.value = currentMap

        viewModelScope.launch {
            try {
                firestoreRepository.sendMessage(groupId, userMessage)
            } catch (_: Exception) {}

            // Trigger medical professional moderator response simulation
            if (group != null) {
                _isSendingGroupMessage.value = true
                val prompt = "Patient ($displayName) says in the group '${group.name}': '$text'"
                val systemInstructions = """
                    You are ${group.moderatedBy}, ${group.moderatorTitle}, serving as the official clinical moderator for the support group '${group.name}'.
                    The group description is: '${group.description}'.
                    A member posted a message (either anonymously or identified). Provide a compassionate, clinically sound, encouraging, and supportive response as the group moderator.
                    Keep it professional, empathetic, concise (2-4 sentences), and remind members that group discussions supplement but do not replace direct 1-on-1 advice from their primary transplant care team.
                """.trimIndent()

                val groupHistory = list.map { msg ->
                    Content(
                        parts = listOf(Part(text = "${if (msg.isAnonymous) msg.anonymousAlias else msg.senderName}: ${msg.text}")),
                        role = if (msg.isModerator) "model" else "user"
                    )
                }

                val modResponseText = try {
                    geminiRepository.getCounselingResponse(prompt, groupHistory, systemInstructions)
                } catch (e: Exception) {
                    "Thank you for sharing with the group. Please remember to reach out to your clinical care coordinator if you experience any acute changes in symptoms."
                }

                val modMessage = ChatMessage(
                    id = "g_msg_mod_${System.currentTimeMillis()}",
                    groupId = groupId,
                    senderId = "mod_${group.id}",
                    senderName = group.moderatedBy,
                    isAnonymous = false,
                    isModerator = true,
                    moderatorBadge = group.moderatorTitle,
                    text = modResponseText,
                    timestamp = System.currentTimeMillis()
                )

                val updatedMap = _groupMessages.value.toMutableMap()
                val updatedList = updatedMap[groupId]?.toMutableList() ?: mutableListOf()
                updatedList.add(modMessage)
                updatedMap[groupId] = updatedList
                _groupMessages.value = updatedMap
                _isSendingGroupMessage.value = false
            }
        }
    }

    fun toggleSessionRsvp(sessionId: String) {
        val current = _scheduledSessions.value.toMutableList()
        val index = current.indexOfFirst { it.id == sessionId }
        if (index != -1) {
            val session = current[index]
            val newIsRsvped = !session.isRsvped
            val newCount = if (newIsRsvped) session.attendeesCount + 1 else session.attendeesCount - 1
            current[index] = session.copy(isRsvped = newIsRsvped, attendeesCount = newCount)
            _scheduledSessions.value = current

            viewModelScope.launch {
                if (newIsRsvped) {
                    _notificationEvent.emit("RSVP confirmed! Reminder set for ${session.title}.")
                } else {
                    _notificationEvent.emit("RSVP cancelled for ${session.title}.")
                }
            }
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

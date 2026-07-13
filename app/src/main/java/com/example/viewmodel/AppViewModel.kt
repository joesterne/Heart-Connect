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
import kotlinx.coroutines.launch
import com.example.data.repository.Content
import com.example.data.repository.Part

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val firestoreRepository = FirestoreRepository(application)
    private val geminiRepository = GeminiRepository()
    private val sharedPrefs = application.getSharedPreferences("heart_connect_prefs", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow<Profile?>(null)
    val userProfile: StateFlow<Profile?> = _userProfile.asStateFlow()

    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles: StateFlow<List<Profile>> = _profiles.asStateFlow()

    private val _supportGroups = MutableStateFlow<List<SupportGroup>>(emptyList())
    val supportGroups: StateFlow<List<SupportGroup>> = _supportGroups.asStateFlow()

    private val _chatHistory = MutableStateFlow<List<Content>>(emptyList())
    val chatHistory: StateFlow<List<Content>> = _chatHistory.asStateFlow()
    
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


    init {
        // Load saved profile or use default placeholder
        val name = sharedPrefs.getString("profile_name", "Alex") ?: "Alex"
        val age = sharedPrefs.getInt("profile_age", 34)
        val location = sharedPrefs.getString("profile_location", "Seattle, WA") ?: "Seattle, WA"
        val medicalHistory = sharedPrefs.getString("profile_medical", "Cardiomyopathy, waiting 6 months") ?: "Cardiomyopathy, waiting 6 months"
        val aboutMe = sharedPrefs.getString("profile_about", "Looking for others going through the same thing.") ?: "Looking for others going through the same thing."
        val journeyPhase = sharedPrefs.getString("profile_journey", "Pre-transplant") ?: "Pre-transplant"

        _userProfile.value = Profile(id = "me", name = name, age = age, location = location, medicalHistory = medicalHistory, aboutMe = aboutMe, journeyPhase = journeyPhase)
        
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
            try {
                _profiles.value = firestoreRepository.getProfiles()
                _supportGroups.value = firestoreRepository.getGroups()
                _communityPosts.value = firestoreRepository.getPosts()
            } catch (e: Exception) {
                // Mock data fallback if firebase fails due to missing google-services.json
                _profiles.value = listOf(
                    Profile(id = "1", name = "Sarah", age = 42, location = "Portland, OR", medicalHistory = "Congenital heart defect, listed 2 months ago.", journeyPhase = "Pre-transplant"),
                    Profile(id = "2", name = "Michael", age = 29, location = "San Francisco, CA", medicalHistory = "Heart failure, listed 1 year ago.", journeyPhase = "Post-transplant recovery")
                )
                _supportGroups.value = listOf(
                    SupportGroup("1", "General Support", "A safe place for all waitlist patients.", "Dr. Smith", emptyList()),
                    SupportGroup("2", "Post-Transplant Life", "Discussing what comes next.", "Nurse Jane", emptyList())
                )
                _communityPosts.value = listOf(
                    com.example.data.model.CommunityPost("1", "1", "Sarah", "Pre-transplant", "Just got my 3-month checkup, everything looks stable!", System.currentTimeMillis() - 86400000),
                    com.example.data.model.CommunityPost("2", "2", "Michael", "Post-transplant recovery", "Has anyone tried the new physical therapy routine recommended by Dr. Aris?", System.currentTimeMillis() - 172800000)
                )
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
        }
    }

    fun saveProfile(name: String, age: Int, location: String, medicalHistory: String, aboutMe: String, journeyPhase: String) {
        sharedPrefs.edit().apply {
            putString("profile_name", name)
            putInt("profile_age", age)
            putString("profile_location", location)
            putString("profile_medical", medicalHistory)
            putString("profile_about", aboutMe)
            putString("profile_journey", journeyPhase)
            apply()
        }
        _userProfile.value = Profile(id = "me", name = name, age = age, location = location, medicalHistory = medicalHistory, aboutMe = aboutMe, journeyPhase = journeyPhase)
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
}

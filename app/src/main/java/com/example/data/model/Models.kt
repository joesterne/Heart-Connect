package com.example.data.model

data class Profile(
    val id: String = "",
    val name: String = "",
    val age: Int = 0,
    val location: String = "",
    val medicalHistory: String = "",
    val aboutMe: String = "",
    val journeyPhase: String = "Not specified"
)

data class SupportGroup(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val moderatedBy: String = "",
    val members: List<String> = emptyList()
)

data class ChatMessage(
    val id: String = "",
    val groupId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class CommunityPost(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorJourneyPhase: String = "Not specified",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

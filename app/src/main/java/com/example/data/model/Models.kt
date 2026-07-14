package com.example.data.model

data class DailyLog(
    val id: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val mood: Int = 3, // 1-5 scale (1: terrible, 5: great)
    val symptoms: String = "",
    val notes: String = ""
)

data class Profile(
    val id: String = "",
    val name: String = "",
    val age: Int = 0,
    val location: String = "",
    val medicalHistory: String = "",
    val aboutMe: String = "",
    val journeyPhase: String = "Not specified",
    val isAvailableForMentorship: Boolean = false,
    val dailyLogs: List<DailyLog> = emptyList()
)

data class EducationalContent(
    val id: String,
    val title: String,
    val description: String,
    val type: String,
    val duration: String
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

data class QAAnswer(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isVerified: Boolean = false
)

data class QAInquiry(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val question: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val answers: List<QAAnswer> = emptyList()
)

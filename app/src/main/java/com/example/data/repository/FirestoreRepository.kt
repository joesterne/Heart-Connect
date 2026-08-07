package com.example.data.repository

import android.content.Context
import com.example.data.model.ChatMessage
import com.example.data.model.Profile
import com.example.data.model.SupportGroup
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val context: Context) {
    private val db: FirebaseFirestore? by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveProfile(profile: Profile) {
        val database = db ?: throw IllegalStateException("Firestore is not available")
        database.collection("profiles").document(profile.id).set(profile).await()
    }

    suspend fun getProfile(userId: String): Profile? {
        val database = db ?: throw IllegalStateException("Firestore is not available")
        return database.collection("profiles").document(userId).get().await().toObject(Profile::class.java)
    }

    suspend fun getProfiles(): List<Profile> {
        val database = db ?: throw IllegalStateException("Firestore is not available")
        return database.collection("profiles").get().await().toObjects(Profile::class.java)
    }

    suspend fun getGroups(): List<SupportGroup> {
        val database = db ?: throw IllegalStateException("Firestore is not available")
        val groups = database.collection("groups").get().await().toObjects(SupportGroup::class.java)
        if (groups.isEmpty()) {
            return listOf(
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
        }
        return groups
    }

    suspend fun sendMessage(groupId: String, message: ChatMessage) {
        val database = db ?: throw IllegalStateException("Firestore is not available")
        database.collection("groups").document(groupId).collection("messages").document(message.id).set(message).await()
    }

    suspend fun getPosts(): List<com.example.data.model.CommunityPost> {
        val database = db ?: throw IllegalStateException("Firestore is not available")
        return database.collection("posts")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(50)
            .get().await().toObjects(com.example.data.model.CommunityPost::class.java)
    }

    suspend fun createPost(post: com.example.data.model.CommunityPost) {
        val database = db ?: throw IllegalStateException("Firestore is not available")
        database.collection("posts").document(post.id).set(post).await()
    }
}

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
                SupportGroup("1", "General Support", "A safe place for all waitlist patients.", "Dr. Smith", emptyList()),
                SupportGroup("2", "Post-Transplant Life", "Discussing what comes next.", "Nurse Jane", emptyList())
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
        return database.collection("posts").get().await().toObjects(com.example.data.model.CommunityPost::class.java).sortedByDescending { it.timestamp }
    }

    suspend fun createPost(post: com.example.data.model.CommunityPost) {
        val database = db ?: throw IllegalStateException("Firestore is not available")
        database.collection("posts").document(post.id).set(post).await()
    }
}

with open("app/src/main/java/com/example/data/repository/FirestoreRepository.kt", "r") as f:
    content = f.read()

old_query = """    suspend fun getPosts(): List<com.example.data.model.CommunityPost> {
        val database = db ?: throw IllegalStateException("Firestore is not available")
        return database.collection("posts").get().await().toObjects(com.example.data.model.CommunityPost::class.java).sortedByDescending { it.timestamp }
    }"""

new_query = """    suspend fun getPosts(): List<com.example.data.model.CommunityPost> {
        val database = db ?: throw IllegalStateException("Firestore is not available")
        return database.collection("posts")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(50)
            .get().await().toObjects(com.example.data.model.CommunityPost::class.java)
    }"""

content = content.replace(old_query, new_query)

with open("app/src/main/java/com/example/data/repository/FirestoreRepository.kt", "w") as f:
    f.write(content)

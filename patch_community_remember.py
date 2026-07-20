with open("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", "r") as f:
    content = f.read()

old_filter = """val filteredPosts = posts.filter { post ->
        val phaseMatch = selectedFilter == "All Phases" || post.authorJourneyPhase == selectedFilter
        val searchMatch = searchQuery.isBlank() || 
            post.content.contains(searchQuery, ignoreCase = true) || 
            post.authorName.contains(searchQuery, ignoreCase = true)
        phaseMatch && searchMatch
    }"""

new_filter = """val filteredPosts = remember(posts, selectedFilter, searchQuery) {
        posts.filter { post ->
            val phaseMatch = selectedFilter == "All Phases" || post.authorJourneyPhase == selectedFilter
            val searchMatch = searchQuery.isBlank() || 
                post.content.contains(searchQuery, ignoreCase = true) || 
                post.authorName.contains(searchQuery, ignoreCase = true)
            phaseMatch && searchMatch
        }
    }"""

content = content.replace(old_filter, new_filter)

with open("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", "w") as f:
    f.write(content)

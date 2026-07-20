sed -i '/item {/i \
            if (recommendedMentors.isNotEmpty()) {\
                item {\
                    Text("Recommended Mentors", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)\
                    Text("Matched based on shared experiences", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)\
                }\
                items(items = recommendedMentors, key = { "mentor_${it.id}" }) { profile ->\
                    ProfileCard(profile = profile, onClick = { onNavigateToPrivateChat(profile.id) })\
                }\
                item { Spacer(Modifier.height(8.dp)) }\
            }\
' app/src/main/java/com/example/ui/screens/DashboardScreen.kt

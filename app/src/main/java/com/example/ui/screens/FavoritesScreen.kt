package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val posts by viewModel.communityPosts.collectAsState()
    val educationalContents by viewModel.educationalContent.collectAsState()
    val savedPosts by viewModel.savedPosts.collectAsState()
    val savedEducationalContent by viewModel.savedEducationalContent.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    val savedPostsList = posts.filter { savedPosts.contains(it.id) }
    val savedEduList = educationalContents.filter { savedEducationalContent.contains(it.id) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Educational") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Community Posts") }
                )
            }
            if (selectedTabIndex == 0) {
                if (savedEduList.isEmpty()) {
                    EmptyStateMessage("No saved educational resources.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(savedEduList) { content ->
                            EducationalCard(
                                content = content,
                                isSaved = true,
                                onToggleSave = { viewModel.toggleSavedEducationalContent(content.id) }
                            )
                        }
                    }
                }
            } else {
                if (savedPostsList.isEmpty()) {
                    EmptyStateMessage("No saved community posts.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(savedPostsList) { post ->
                            PostCard(
                                post = post,
                                isSaved = true,
                                onToggleSave = { viewModel.toggleSavedPost(post.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

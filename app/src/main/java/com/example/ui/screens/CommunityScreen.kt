package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.SupportGroup
import com.example.viewmodel.AppViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.data.model.CommunityPost
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val groups by viewModel.supportGroups.collectAsState()
    val posts by viewModel.communityPosts.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community") },
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
                    text = { Text("Feed") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Groups") }
                )
            }
            if (selectedTabIndex == 0) {
                CommunityFeed(posts = posts, onCreatePost = { viewModel.createCommunityPost(it) })
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text("Anonymous Moderated Support Groups", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Encrypted & safe environment for peer counseling.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(groups) { group ->
                        GroupCard(group)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityFeed(posts: List<CommunityPost>, onCreatePost: (String) -> Unit) {
    var newPostText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Phases") }
    var filterExpanded by remember { mutableStateOf(false) }
    
    val journeyPhases = listOf("All Phases", "Pre-transplant", "Waitlisted", "Post-transplant recovery", "Long-term survivor", "Caregiver")
    val filteredPosts = if (selectedFilter == "All Phases") posts else posts.filter { it.authorJourneyPhase == selectedFilter }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = newPostText,
                    onValueChange = { newPostText = it },
                    placeholder = { Text("Share your experience or ask a question...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ExposedDropdownMenuBox(
                        expanded = filterExpanded,
                        onExpandedChange = { filterExpanded = !filterExpanded },
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = selectedFilter,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filter by Phase") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(24.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = filterExpanded,
                            onDismissRequest = { filterExpanded = false }
                        ) {
                            journeyPhases.forEach { phase ->
                                DropdownMenuItem(
                                    text = { Text(phase) },
                                    onClick = {
                                        selectedFilter = phase
                                        filterExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Button(
                        onClick = {
                            if (newPostText.isNotBlank()) {
                                onCreatePost(newPostText)
                                newPostText = ""
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("Post")
                    }
                }
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredPosts) { post ->
                PostCard(post)
            }
        }
    }
}

@Composable
fun PostCard(post: CommunityPost) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.authorName.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(post.authorName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(
                        SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault()).format(Date(post.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = post.authorJourneyPhase,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(post.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun GroupCard(group: SupportGroup) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = "Encrypted", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(group.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Text(group.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text("Moderator: ${group.moderatedBy}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { /* Join Logic */ },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
            ) {
                Text("Join Group")
            }
        }
    }
}

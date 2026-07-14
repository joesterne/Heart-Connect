package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val groups by viewModel.supportGroups.collectAsState()
    val posts by viewModel.communityPosts.collectAsState()
    val savedPosts by viewModel.savedPosts.collectAsState()
    val qaInquiries by viewModel.qaInquiries.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
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
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Medical Q&A") }
                )
                Tab(
                    selected = selectedTabIndex == 3,
                    onClick = { selectedTabIndex = 3 },
                    text = { Text("Map") }
                )
            }
            when (selectedTabIndex) {
                0 -> {
                    CommunityFeed(
                        posts = posts, 
                        savedPosts = savedPosts,
                        onToggleSave = { viewModel.toggleSavedPost(it) },
                        onCreatePost = { viewModel.createCommunityPost(it) }
                    )
                }
                1 -> {
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
                2 -> {
                    QAFeed(
                        inquiries = qaInquiries,
                        isMentor = userProfile?.isAvailableForMentorship == true,
                        onCreateInquiry = { viewModel.createQAInquiry(it) },
                        onAnswerInquiry = { id, answer -> viewModel.answerQAInquiry(id, answer) },
                        onVerifyAnswer = { qId, aId -> viewModel.verifyQAAnswer(qId, aId) }
                    )
                }
                3 -> {
                    CentersMap()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityFeed(
    posts: List<CommunityPost>,
    savedPosts: Set<String>,
    onToggleSave: (String) -> Unit,
    onCreatePost: (String) -> Unit
) {
    var newPostText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Phases") }
    var filterExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    val journeyPhases = listOf("All Phases", "Pre-transplant", "Waitlisted", "Post-transplant recovery", "Long-term survivor", "Caregiver")
    val filteredPosts = posts.filter { post ->
        val phaseMatch = selectedFilter == "All Phases" || post.authorJourneyPhase == selectedFilter
        val searchMatch = searchQuery.isBlank() || 
            post.content.contains(searchQuery, ignoreCase = true) || 
            post.authorName.contains(searchQuery, ignoreCase = true)
        phaseMatch && searchMatch
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search posts or members...") },
                    leadingIcon = { Icon(androidx.compose.material.icons.Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
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
                PostCard(
                    post = post,
                    isSaved = savedPosts.contains(post.id),
                    onToggleSave = { onToggleSave(post.id) }
                )
            }
        }
    }
}

@Composable
fun PostCard(
    post: CommunityPost,
    isSaved: Boolean = false,
    onToggleSave: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.authorName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(
                        SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault()).format(Date(post.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (onToggleSave != null) {
                    IconButton(onClick = onToggleSave) {
                        Icon(
                            imageVector = if (isSaved) androidx.compose.material.icons.Icons.Default.Bookmark else androidx.compose.material.icons.Icons.Default.BookmarkBorder,
                            contentDescription = if (isSaved) "Unsave" else "Save",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QAFeed(
    inquiries: List<com.example.data.model.QAInquiry>,
    isMentor: Boolean,
    onCreateInquiry: (String) -> Unit,
    onAnswerInquiry: (String, String) -> Unit,
    onVerifyAnswer: (String, String) -> Unit
) {
    var newQuestionText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = newQuestionText,
                    onValueChange = { newQuestionText = it },
                    placeholder = { Text("Ask a medical question...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    minLines = 3,
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = {
                            if (newQuestionText.isNotBlank()) {
                                onCreateInquiry(newQuestionText)
                                newQuestionText = ""
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("Post Question")
                    }
                }
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(inquiries) { inquiry ->
                QACard(
                    inquiry = inquiry,
                    isMentor = isMentor,
                    onAnswer = { onAnswerInquiry(inquiry.id, it) },
                    onVerify = { aId -> onVerifyAnswer(inquiry.id, aId) }
                )
            }
        }
    }
}

@Composable
fun QACard(
    inquiry: com.example.data.model.QAInquiry,
    isMentor: Boolean,
    onAnswer: (String) -> Unit,
    onVerify: (String) -> Unit
) {
    var showAnswerField by remember { mutableStateOf(false) }
    var answerText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(inquiry.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Asked by ${inquiry.authorName} • ${SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(inquiry.timestamp))}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            inquiry.answers.forEach { answer ->
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    color = if (answer.isVerified) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(answer.authorName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            if (answer.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Verified, contentDescription = "Verified", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                Text(" Verified", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(answer.content, style = MaterialTheme.typography.bodyMedium)
                        
                        if (isMentor && !answer.isVerified) {
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { onVerify(answer.id) }) {
                                Text("Mark as Verified")
                            }
                        }
                    }
                }
            }

            if (showAnswerField) {
                OutlinedTextField(
                    value = answerText,
                    onValueChange = { answerText = it },
                    placeholder = { Text("Your answer...") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { showAnswerField = false; answerText = "" }) {
                        Text("Cancel")
                    }
                    Button(onClick = { 
                        if (answerText.isNotBlank()) {
                            onAnswer(answerText)
                            showAnswerField = false
                            answerText = ""
                        }
                    }) {
                        Text("Submit")
                    }
                }
            } else {
                TextButton(onClick = { showAnswerField = true }, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Add an Answer")
                }
            }
        }
    }
}

@Composable
fun CentersMap() {
    val defaultLocation = LatLng(37.7749, -122.4194) // Default to SF
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    val centers = listOf(
        Pair(LatLng(37.7694, -122.4862), "UCSF Medical Center"),
        Pair(LatLng(37.4300, -122.1700), "Stanford Hospital"),
        Pair(LatLng(37.7944, -122.4000), "Community Support Group A")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                "Find nearby transplant centers and support groups. (Requires Maps API Key in Settings)",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Card(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp).padding(bottom = 16.dp), shape = RoundedCornerShape(24.dp)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                centers.forEach { (latLng, title) ->
                    Marker(
                        state = MarkerState(position = latLng),
                        title = title,
                        snippet = "Transplant Support"
                    )
                }
            }
        }
    }
}

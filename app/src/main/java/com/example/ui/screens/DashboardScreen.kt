package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.Profile
import com.example.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AppViewModel,
    onNavigateToCounseling: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToEducation: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToMetrics: () -> Unit,
    onNavigateToPrivateChat: (String) -> Unit
) {
    val profiles by viewModel.profiles.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val recommendedMentors by viewModel.recommendedMentors.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    val openDrawer = com.example.ui.navigation.LocalOpenDrawer.current
                    IconButton(onClick = openDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                title = { Text("Heart Connect", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Edit Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_banner),
                            contentDescription = "Hero banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f))
                        )
                        Column(
                            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                        ) {
                            Text("You are not alone.", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                            Text("Find strength in community.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }

                        item {
                val isHighContrast by viewModel.isHighContrast.collectAsState()
                Card(
                    modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {
                        contentDescription = "Accessibility options"
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("High Contrast Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Improves readability for visual accessibility", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = isHighContrast,
                            onCheckedChange = { viewModel.toggleHighContrast(it) },
                            modifier = Modifier.semantics { contentDescription = "Toggle high contrast mode" }
                        )
                    }
                }
            }

            userProfile?.let { myProfile ->
                item {
                    Text("My Matching Profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.img_avatar_placeholder),
                                contentDescription = null,
                                modifier = Modifier.size(56.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(myProfile.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("${myProfile.age} yrs • ${myProfile.location}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(myProfile.medicalHistory, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = onNavigateToCounseling,
                        modifier = Modifier.weight(1f).height(80.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                            Spacer(Modifier.height(4.dp))
                            Text("AI Guide", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    Button(
                        onClick = onNavigateToCommunity,
                        modifier = Modifier.weight(1f).height(80.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Group, contentDescription = null)
                            Spacer(Modifier.height(4.dp))
                            Text("Community", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    Button(
                        onClick = onNavigateToEducation,
                        modifier = Modifier.weight(1f).height(80.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                            Spacer(Modifier.height(4.dp))
                            Text("Education", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            
            item {
                Button(
                    onClick = onNavigateToMetrics,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.MonitorHeart, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Health Metrics Dashboard")
                }
            }
            item {
                Button(
                    onClick = onNavigateToFavorites,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.Bookmark, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Saved Favorites")
                }
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Medical Info & Waitlist Advice") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.searchMedicalInfo(searchQuery) }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )
            }

            if (isSearching) {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            } else if (searchResults.isNotEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                                Spacer(Modifier.width(8.dp))
                                Text("Medical Resources", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(searchResults, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(Modifier.width(8.dp))
                            Text("Essential Resources & Contacts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                        Spacer(Modifier.height(16.dp))
                        ResourceContactItem(title = "UNOS Patient Services", info = "1-888-894-6361", desc = "Information about transplant and donation.")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f))
                        ResourceContactItem(title = "American Heart Association", info = "1-800-AHA-USA-1", desc = "Support groups and cardiovascular resources.")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f))
                        ResourceContactItem(title = "Transplant Recipients International Org.", info = "info@trioweb.org", desc = "Peer support and advocacy.")
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            if (recommendedMentors.isNotEmpty()) {
                item {
                    Text("Recommended Mentors", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Matched based on shared experiences", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(items = recommendedMentors, key = { "mentor_${it.id}" }) { profile ->
                    ProfileCard(profile = profile, onClick = { onNavigateToPrivateChat(profile.id) })
                }
                item { Spacer(Modifier.height(8.dp)) }
            }

            item {
                Text("Recently Active Members", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            items(items = profiles, key = { it.id }) { profile ->
                ProfileCard(profile = profile, onClick = { onNavigateToPrivateChat(profile.id) })
            }
        }
    }
}

@Composable
fun ProfileCard(profile: Profile, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {
            contentDescription = "Profile card for ${profile.name}, ${profile.age} years old in ${profile.location}. Medical history: ${profile.medicalHistory}."
        },
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_avatar_placeholder),
                contentDescription = null,
                modifier = Modifier.size(56.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(profile.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${profile.age} yrs • ${profile.location}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(profile.medicalHistory, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (profile.badges.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        profile.badges.forEach { badge ->
                            val icon = when (badge.iconResName) {
                                "VolunteerActivism" -> Icons.Default.VolunteerActivism
                                "Favorite" -> Icons.Default.Favorite
                                "LocalFireDepartment" -> Icons.Default.LocalFireDepartment
                                "VerifiedUser" -> Icons.Default.VerifiedUser
                                else -> Icons.Default.Star
                            }
                            Icon(icon, contentDescription = badge.name, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "E2E Secure",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ResourceContactItem(title: String, info: String, desc: String) {
    Column(modifier = Modifier.semantics(mergeDescendants = true) {
        contentDescription = "$title, contact: $info. $desc"
    }) {
        Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSecondaryContainer)
        Text(info, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
        Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
    }
}

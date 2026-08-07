package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ChatMessage
import com.example.data.model.SupportGroup
import com.example.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChatScreen(
    viewModel: AppViewModel,
    groupId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val groups by viewModel.supportGroups.collectAsState()
    val allGroupMessages by viewModel.groupMessages.collectAsState()
    val isSending by viewModel.isSendingGroupMessage.collectAsState()

    val group = groups.find { it.id == groupId } ?: SupportGroup(
        id = groupId,
        name = "Support Circle",
        description = "A moderated peer support group for heart transplant patients.",
        moderatedBy = "Dr. Aris Thorne, MD",
        moderatorTitle = "Transplant Cardiologist",
        category = "Peer Counseling",
        rules = listOf("Respect privacy", "Anonymous posts permitted", "No clinical prescriptions")
    )

    val messages = allGroupMessages[groupId] ?: emptyList()
    val listState = rememberLazyListState()

    var isAnonymous by remember { mutableStateOf(group.isAnonymousByDefault) }
    var anonymousAliasNumber by remember { mutableStateOf((100..999).random()) }
    var customAlias by remember { mutableStateOf("HopefulHeart_$anonymousAliasNumber") }
    var messageText by remember { mutableStateOf("") }
    var showRulesDialog by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (showRulesDialog) {
        AlertDialog(
            onDismissRequest = { showRulesDialog = false },
            icon = { Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Group Rules & Medical Oversight") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Clinical Moderator: ${group.moderatedBy}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(group.moderatorTitle, style = MaterialTheme.typography.bodySmall)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("Guidelines:", fontWeight = FontWeight.SemiBold)
                    group.rules.forEach { rule ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text("• ", fontWeight = FontWeight.Bold)
                            Text(rule, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Note: Medical professionals oversee discussions to ensure safety and provide guidance. In case of medical emergencies, call emergency services immediately.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showRulesDialog = false }) {
                    Text("Got It")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                group.name,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MedicalServices,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Moderated by ${group.moderatedBy}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("group_chat_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showRulesDialog = true }, modifier = Modifier.testTag("group_chat_info_button")) {
                        Icon(Icons.Default.Info, contentDescription = "Group Guidelines")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Medical Professional Oversight Header Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Verified Moderator",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Moderated Peer Support",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    group.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            "${group.moderatedBy} (${group.moderatorTitle}) oversees this group for clinical safety and emotional support.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Anonymous Post Controls Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isAnonymous) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Anonymity status",
                        tint = if (isAnonymous) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAnonymous) "Participating Anonymously" else "Posting as Yourself",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (isAnonymous) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Alias: $customAlias",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "New Alias",
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable {
                                            anonymousAliasNumber = (100..999).random()
                                            customAlias = "HopefulHeart_$anonymousAliasNumber"
                                        },
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                    Switch(
                        checked = isAnonymous,
                        onCheckedChange = { isAnonymous = it },
                        modifier = Modifier.testTag("anonymous_toggle_switch")
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Chat Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = messages, key = { it.id }) { msg ->
                    GroupChatMessageItem(message = msg)
                }

                if (isSending) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Medical moderator is preparing guidance...",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Quick Discussion Starters
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 6.dp)
            ) {
                Text(
                    "Suggested Group Topics:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SuggestionChip(
                        onClick = { messageText = "How do you manage waitlist anxiety during sleep?" },
                        label = { Text("Sleep & Anxiety", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("suggestion_chip_1")
                    )
                    SuggestionChip(
                        onClick = { messageText = "What questions should I ask Dr. Thorne at my next review?" },
                        label = { Text("Doctor Visit Prep", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("suggestion_chip_2")
                    )
                    SuggestionChip(
                        onClick = { messageText = "Sharing a quick encouraging update with everyone today!" },
                        label = { Text("Encouragement", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("suggestion_chip_3")
                    )
                }
            }

            // Input Row
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("group_chat_input_field"),
                        placeholder = {
                            Text(
                                if (isAnonymous) "Share anonymously as $customAlias..." else "Share message with group..."
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4
                    )
                    Spacer(Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick = {
                            if (messageText.trim().isEmpty()) {
                                Toast.makeText(context, "Please enter a message.", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.sendGroupMessage(
                                    groupId = groupId,
                                    text = messageText.trim(),
                                    isAnonymous = isAnonymous,
                                    customAlias = customAlias
                                )
                                messageText = ""
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.testTag("group_chat_send_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Group Message")
                    }
                }
            }
        }
    }
}

@Composable
fun GroupChatMessageItem(message: ChatMessage) {
    val isModerator = message.isModerator
    val isAnonymous = message.isAnonymous
    val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = if (isModerator) Alignment.Start else Alignment.End
    ) {
        Card(
            modifier = Modifier.widthIn(max = 320.dp),
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isModerator) 4.dp else 18.dp,
                bottomEnd = if (isModerator) 18.dp else 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    isModerator -> MaterialTheme.colorScheme.primaryContainer
                    isAnonymous -> MaterialTheme.colorScheme.tertiaryContainer
                    else -> MaterialTheme.colorScheme.secondaryContainer
                }
            ),
            elevation = CardDefaults.cardElevation(if (isModerator) 3.dp else 1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Header (Sender info & badge)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isModerator) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "Verified Moderator",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                message.senderName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else if (isAnonymous) {
                            Icon(
                                Icons.Default.VisibilityOff,
                                contentDescription = "Anonymous",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                if (message.anonymousAlias.isNotBlank()) message.anonymousAlias else "Anonymous Member",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        } else {
                            Text(
                                message.senderName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    Text(
                        formattedTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isModerator && message.moderatorBadge.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "🩺 ${message.moderatorBadge}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else if (isAnonymous) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "🔒 Anonymous Peer Post",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                Spacer(Modifier.height(6.dp))
                Text(
                    message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        isModerator -> MaterialTheme.colorScheme.onPrimaryContainer
                        isAnonymous -> MaterialTheme.colorScheme.onTertiaryContainer
                        else -> MaterialTheme.colorScheme.onSecondaryContainer
                    }
                )
            }
        }
    }
}

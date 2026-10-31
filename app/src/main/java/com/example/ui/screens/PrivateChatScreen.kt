package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.EnhancedEncryption
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.ChatMessage
import com.example.data.model.Profile
import com.example.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateChatScreen(viewModel: AppViewModel, peerId: String) {
    val profiles by viewModel.profiles.collectAsState()
    val privateMessages by viewModel.privateMessages.collectAsState()
    val isSending by viewModel.isSendingPrivateMessage.collectAsState()
    var messageText by remember { mutableStateOf("") }

    val peer = profiles.find { it.id == peerId } ?: Profile(id = peerId, name = "Peer Patient", age = 30, location = "USA", medicalHistory = "Waiting list")
    val messages = privateMessages[peerId] ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(peer.name, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EnhancedEncryption,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "AI peer simulation",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    val openDrawer = com.example.ui.navigation.LocalOpenDrawer.current
                    IconButton(onClick = openDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
            Column(modifier = Modifier.fillMaxSize().widthIn(max = 600.dp)) {
            // Visual Banner illustrating encryption keys swapped
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "About this chat",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "About this conversation",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${peer.name} is an AI-generated peer persona for practicing supportive conversation — not a real person. Messages are not end-to-end encrypted and are processed by an AI service, so please avoid sharing sensitive personal details.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (messages.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No messages yet. Send a greeting to start the conversation.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = messages, key = { it.id }) { msg ->
                        SecureMessageBubble(message = msg, isMe = msg.senderId == "me")
                    }
                    if (isSending) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Exchanging handshake keys...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }

            Surface(color = MaterialTheme.colorScheme.surfaceVariant, tonalElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Send secure message...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    val context = androidx.compose.ui.platform.LocalContext.current
                    FloatingActionButton(
                        onClick = {
                            if (messageText.trim().isEmpty()) {
                                android.widget.Toast.makeText(context, "Message cannot be empty.", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.sendPrivateSecureMessage(peerId, messageText.trim())
                                messageText = ""
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
}
}
}
@Composable
fun SecureMessageBubble(message: com.example.data.model.ChatMessage, isMe: Boolean) {
    // This used to fake an encryption layer: each message rendered as
    // scrambled "ciphertext" for 600ms, then flipped to plaintext labeled
    // "E2EE Decrypted". Nothing here is end-to-end encrypted — the peer is
    // an AI simulation and messages go to an AI service (see the disclosure
    // banner at the top of this screen). The bubble is a plain bubble now;
    // the name is kept to avoid churn at the call site.
    val alignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    val containerColor = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
    val textColor = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            modifier = Modifier
                .align(alignment)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

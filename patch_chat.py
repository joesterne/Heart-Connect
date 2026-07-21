with open("app/src/main/java/com/example/ui/screens/PrivateChatScreen.kt", "r") as f:
    content = f.read()

old_chat = '''                    FloatingActionButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendPrivateSecureMessage(peerId, messageText)
                                messageText = ""
                            }
                        },'''

new_chat = '''                    val context = androidx.compose.ui.platform.LocalContext.current
                    FloatingActionButton(
                        onClick = {
                            if (messageText.trim().isEmpty()) {
                                android.widget.Toast.makeText(context, "Message cannot be empty.", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.sendPrivateSecureMessage(peerId, messageText.trim())
                                messageText = ""
                            }
                        },'''

content = content.replace(old_chat, new_chat)

with open("app/src/main/java/com/example/ui/screens/PrivateChatScreen.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/screens/AICounselingScreen.kt", "r") as f:
    content = f.read()

old_ai = '''                    FloatingActionButton(
                        onClick = { 
                             if(message.isNotBlank()) {
                                viewModel.sendMessageToCounselor(message)
                                message = ""
                            }
                        },'''

new_ai = '''                    val context = androidx.compose.ui.platform.LocalContext.current
                    FloatingActionButton(
                        onClick = { 
                             if (message.trim().isEmpty()) {
                                android.widget.Toast.makeText(context, "Message cannot be empty.", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.sendMessageToCounselor(message.trim())
                                message = ""
                            }
                        },'''

content = content.replace(old_ai, new_ai)

with open("app/src/main/java/com/example/ui/screens/AICounselingScreen.kt", "w") as f:
    f.write(content)

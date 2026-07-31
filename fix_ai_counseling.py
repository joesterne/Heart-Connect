import os
import re

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
                             if(message.trim().isEmpty()) {
                                android.widget.Toast.makeText(context, "Message cannot be empty.", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.sendMessageToCounselor(message.trim())
                                message = ""
                            }
                        },'''

content = content.replace(old_ai, new_ai)

with open("app/src/main/java/com/example/ui/screens/AICounselingScreen.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", "r") as f:
    content = f.read()

old_qa = '''                    Button(onClick = { 
                         if (answerText.isNotBlank()) {
                            onAnswer(answerText)
                            showAnswerField = false
                            answerText = ""
                        }
                    }) {'''

new_qa = '''                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(onClick = { 
                         if (answerText.trim().isEmpty()) {
                             android.widget.Toast.makeText(context, "Answer cannot be empty.", android.widget.Toast.LENGTH_SHORT).show()
                         } else {
                            onAnswer(answerText.trim())
                            showAnswerField = false
                            answerText = ""
                        }
                    }) {'''

content = content.replace(old_qa, new_qa)

with open("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", "w") as f:
    f.write(content)

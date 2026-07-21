with open("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", "r") as f:
    content = f.read()

old_post = '''                    Button(
                        onClick = {
                            if (newPostText.isNotBlank()) {
                                onCreatePost(newPostText)
                                newPostText = ""
                            }
                        },'''

new_post = '''                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(
                        onClick = {
                            if (newPostText.trim().length < 5) {
                                android.widget.Toast.makeText(context, "Post must be at least 5 characters.", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                onCreatePost(newPostText.trim())
                                newPostText = ""
                            }
                        },'''

content = content.replace(old_post, new_post)

old_question = '''                    Button(
                        onClick = {
                            if (newQuestionText.isNotBlank()) {
                                onCreateInquiry(newQuestionText)
                                newQuestionText = ""
                            }
                        },'''

new_question = '''                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(
                        onClick = {
                            if (newQuestionText.trim().length < 10) {
                                android.widget.Toast.makeText(context, "Question must be at least 10 characters.", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                onCreateInquiry(newQuestionText.trim())
                                newQuestionText = ""
                            }
                        },'''

content = content.replace(old_question, new_question)

old_answer = '''                    Button(onClick = { 
                         if (answerText.isNotBlank()) {
                            onAnswer(answerText)
                            showAnswerField = false
                            answerText = ""
                        }
                    }) {'''

new_answer = '''                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(onClick = { 
                         if (answerText.trim().length < 5) {
                            android.widget.Toast.makeText(context, "Answer must be at least 5 characters.", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            onAnswer(answerText.trim())
                            showAnswerField = false
                            answerText = ""
                        }
                    }) {'''

content = content.replace(old_answer, new_answer)

with open("app/src/main/java/com/example/ui/screens/CommunityScreen.kt", "w") as f:
    f.write(content)

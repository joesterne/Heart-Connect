import re

with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "r") as f:
    content = f.read()

bad_block = """            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { isAddingLog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Text Log")
                    }
                    Button(
                        onClick = {
                            if (isRecording) {
                                val base64Audio = audioRecorder?.stopRecording()
                                isRecording = false
                                if (base64Audio != null) {
                                    viewModel.transcribeAndAddAudioLog(base64Audio, mood.toInt(), symptoms)
                                    mood = 3f
                                    symptoms = ""
                                    notes = ""
                                }
                            } else {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                    audioRecorder = AudioRecorderHelper(context)
                                    audioRecorder?.startRecording()
                                    isRecording = true
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        if (isTranscribing) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Icon(if (isRecording) Icons.Default.Stop else Icons.Default.Mic, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isRecording) "Stop" else "Voice")
                        }
                    }
                }
            }"""

parts = content.split(bad_block)

fixed_content = parts[0] + """            } else {
                isAvailableForMentorship = false
            }""" + parts[1] + """            } else {
                viewModel.saveProfile(name, age, location, medicalHistory, aboutMe, journeyPhase, isAvailableForMentorship)
                isEditMode = false
            }""" + parts[2] + """            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)""" + parts[3] + bad_block + parts[4]

with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "w") as f:
    f.write(fixed_content)

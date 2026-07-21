with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "r") as f:
    content = f.read()

secure_ui = """                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Encrypted Backup", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Text("Securely backup and restore your daily logs locally using EncryptedFile.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(
                                onClick = { 
                                    viewModel.backupLogsSecurely() 
                                    Toast.makeText(context, "Logs backed up securely", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Backup")
                            }
                            OutlinedButton(
                                onClick = { 
                                    viewModel.restoreLogsSecurely() 
                                    Toast.makeText(context, "Logs restored securely", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Restore")
                            }
                        }
                    }
                }"""

if "Encrypted Backup" not in content:
    content = content.replace("AccessibilitySettingsSection(viewModel = viewModel)", "AccessibilitySettingsSection(viewModel = viewModel)\n" + secure_ui)

with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "w") as f:
    f.write(content)

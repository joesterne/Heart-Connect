sed -i '/if (recommendedMentors.isNotEmpty()) {/i \
            item {\
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {\
                    Column(modifier = Modifier.padding(16.dp)) {\
                        Row(verticalAlignment = Alignment.CenterVertically) {\
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)\
                            Spacer(Modifier.width(8.dp))\
                            Text("Essential Resources & Contacts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)\
                        }\
                        Spacer(Modifier.height(16.dp))\
                        ResourceContactItem(title = "UNOS Patient Services", info = "1-888-894-6361", desc = "Information about transplant and donation.")\
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f))\
                        ResourceContactItem(title = "American Heart Association", info = "1-800-AHA-USA-1", desc = "Support groups and cardiovascular resources.")\
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f))\
                        ResourceContactItem(title = "Transplant Recipients International Org.", info = "info@trioweb.org", desc = "Peer support and advocacy.")\
                    }\
                }\
                Spacer(Modifier.height(16.dp))\
            }\
' app/src/main/java/com/example/ui/screens/DashboardScreen.kt

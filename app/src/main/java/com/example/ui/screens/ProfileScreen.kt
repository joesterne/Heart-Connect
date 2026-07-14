package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current

    var isEditMode by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var ageString by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var medicalHistory by remember { mutableStateOf("") }
    var aboutMe by remember { mutableStateOf("") }
    var journeyPhase by remember { mutableStateOf("Pre-transplant") }
    var isAvailableForMentorship by remember { mutableStateOf(false) }

    val journeyPhases = listOf("Pre-transplant", "Waitlisted", "Post-transplant recovery", "Long-term survivor", "Caregiver")
    var expandedPhaseDropdown by remember { mutableStateOf(false) }

    // Initialize form values from current profile state
    LaunchedEffect(userProfile, isEditMode) {
        userProfile?.let {
            name = it.name
            ageString = it.age.toString()
            location = it.location
            medicalHistory = it.medicalHistory
            aboutMe = it.aboutMe
            journeyPhase = it.journeyPhase
            isAvailableForMentorship = it.isAvailableForMentorship
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Profile" else "My Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isEditMode) {
                Text(
                    text = "Personal Transplant Journey Profile",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                )

                OutlinedTextField(
                    value = ageString,
                    onValueChange = { ageString = it },
                    label = { Text("Age") },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (City, State)") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                )

                OutlinedTextField(
                    value = medicalHistory,
                    onValueChange = { medicalHistory = it },
                    label = { Text("Transplant Medical History / Status") },
                    leadingIcon = { Icon(Icons.Default.MedicalInformation, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    minLines = 3,
                    maxLines = 5
                )

                OutlinedTextField(
                    value = aboutMe,
                    onValueChange = { aboutMe = it },
                    label = { Text("About Me / Match Preferences") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    minLines = 2,
                    maxLines = 4
                )

                ExposedDropdownMenuBox(
                    expanded = expandedPhaseDropdown,
                    onExpandedChange = { expandedPhaseDropdown = !expandedPhaseDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = journeyPhase,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Journey Phase") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPhaseDropdown) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(24.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPhaseDropdown,
                        onDismissRequest = { expandedPhaseDropdown = false }
                    ) {
                        journeyPhases.forEach { phase ->
                            DropdownMenuItem(
                                text = { Text(phase) },
                                onClick = {
                                    journeyPhase = phase
                                    expandedPhaseDropdown = false
                                }
                            )
                        }
                    }
                }

                if (journeyPhase == "Post-transplant recovery" || journeyPhase == "Long-term survivor") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mentorship Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text("Available to mentor pre-transplant members", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isAvailableForMentorship,
                            onCheckedChange = { isAvailableForMentorship = it }
                        )
                    }
                } else {
                    isAvailableForMentorship = false
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val age = ageString.toIntOrNull()
                        if (name.isBlank() || age == null || location.isBlank() || medicalHistory.isBlank()) {
                            Toast.makeText(context, "Please fill out all fields with valid information.", Toast.LENGTH_LONG).show()
                        } else {
                            viewModel.saveProfile(name, age, location, medicalHistory, aboutMe, journeyPhase, isAvailableForMentorship)
                            Toast.makeText(context, "Profile successfully saved securely to local storage!", Toast.LENGTH_SHORT).show()
                            isEditMode = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save & Sync Profile", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(32.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userProfile?.name?.take(1)?.uppercase() ?: "U",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(userProfile?.name ?: "Unknown User", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("${userProfile?.age ?: "?"} years old • ${userProfile?.location ?: "Unknown location"}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        if (userProfile?.isAvailableForMentorship == true) {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Available for Mentorship", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onTertiaryContainer)
                                }
                            }
                        }
                        
                        HorizontalDivider()
                        
                        Column {
                            Text("Transplant Journey", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(userProfile?.medicalHistory ?: "No medical history provided.", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Current Phase: ${userProfile?.journeyPhase ?: "Not specified"}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        }

                        Column {
                            Text("About Me & Interests", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(userProfile?.aboutMe ?: "No bio provided.", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                DailyTrackerSection(viewModel = viewModel, userProfile = userProfile)

                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { isEditMode = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Edit Profile", style = MaterialTheme.typography.titleMedium)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = {
                        val summaryText = """
                            Transplant Journey Summary
                            --------------------------
                            Name: ${userProfile?.name ?: "N/A"}
                            Age: ${userProfile?.age ?: "N/A"}
                            Location: ${userProfile?.location ?: "N/A"}
                            
                            Current Phase: ${userProfile?.journeyPhase ?: "N/A"}
                            
                            Medical History:
                            ${userProfile?.medicalHistory ?: "N/A"}
                            
                            About Me:
                            ${userProfile?.aboutMe ?: "N/A"}
                        """.trimIndent()
                        
                        val sendIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, summaryText)
                            type = "text/plain"
                        }
                        val shareIntent = android.content.Intent.createChooser(sendIntent, "Export Journey Summary")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Journey Summary", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun DailyTrackerSection(viewModel: AppViewModel, userProfile: com.example.data.model.Profile?) {
    var isAddingLog by remember { mutableStateOf(false) }
    var mood by remember { mutableStateOf(3f) }
    var symptoms by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Daily Mood & Symptoms Tracker", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            
            if (isAddingLog) {
                Text("Mood (1-5): ${mood.toInt()}", style = MaterialTheme.typography.bodyMedium)
                Slider(
                    value = mood,
                    onValueChange = { mood = it },
                    valueRange = 1f..5f,
                    steps = 3
                )
                
                OutlinedTextField(
                    value = symptoms,
                    onValueChange = { symptoms = it },
                    label = { Text("Symptoms") },
                    placeholder = { Text("e.g. fatigue, headache") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { isAddingLog = false }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        viewModel.addDailyLog(mood.toInt(), symptoms, notes)
                        isAddingLog = false
                        mood = 3f
                        symptoms = ""
                        notes = ""
                    }) {
                        Text("Save Log")
                    }
                }
            } else {
                Button(
                    onClick = { isAddingLog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Daily Log")
                }
            }

            userProfile?.let { profile ->
                MoodTrendChart(logs = profile.dailyLogs)
            }

            userProfile?.dailyLogs?.take(3)?.forEach { log ->
                val date = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(log.timestamp))
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(date, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Mood: ${log.mood}/5", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                        if (log.symptoms.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Symptoms: ${log.symptoms}", style = MaterialTheme.typography.bodyMedium)
                        }
                        if (log.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Notes: ${log.notes}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoodTrendChart(logs: List<com.example.data.model.DailyLog>) {
    val thirtyDaysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
    val recentLogs = logs.filter { it.timestamp >= thirtyDaysAgo }.sortedBy { it.timestamp }

    if (recentLogs.isEmpty()) {
        Text("No mood data yet to display a 30-day trend.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }

    val averageMood = recentLogs.map { it.mood }.average()
    val trendText = when {
        averageMood >= 4.0 -> "Consistently positive mood"
        averageMood >= 3.0 -> "Stable mood with some fluctuations"
        averageMood >= 2.0 -> "Challenging period, lower mood"
        else -> "Experiencing significant difficulty"
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Text(
            "30-Day Mood Trend", 
            style = MaterialTheme.typography.titleSmall, 
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Average: ${String.format(java.util.Locale.getDefault(), "%.1f", averageMood)}/5 - $trendText", 
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val maxItems = 30
            val displayLogs = recentLogs.takeLast(maxItems)
            displayLogs.forEach { log ->
                val color = when(log.mood) {
                    5, 4 -> MaterialTheme.colorScheme.primary
                    3 -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.error
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .fillMaxHeight(log.mood / 5f)
                        .background(color, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                )
            }
        }
    }
}

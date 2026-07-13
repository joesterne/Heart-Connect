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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Save
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

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val age = ageString.toIntOrNull()
                        if (name.isBlank() || age == null || location.isBlank() || medicalHistory.isBlank()) {
                            Toast.makeText(context, "Please fill out all fields with valid information.", Toast.LENGTH_LONG).show()
                        } else {
                            viewModel.saveProfile(name, age, location, medicalHistory, aboutMe, journeyPhase)
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
                
                Button(
                    onClick = { isEditMode = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Edit Profile", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

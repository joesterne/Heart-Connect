package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R

@Composable
fun AuthScreen(onSignInSuccess: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_app_icon),
                contentDescription = "App Icon",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Welcome to Heart Connect",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "A secure community for transplant waitlist patients.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(48.dp))
            
            // This button performs NO authentication: it calls
            // onSignInSuccess directly. It used to be labeled "Sign in with
            // Google" with a footer claiming "Data is secured via Firebase
            // Auth" — neither was true (no FirebaseAuth or Credential
            // Manager code exists in the app). It is labeled as the demo
            // entry it actually is until real sign-in is implemented.
            Button(
                onClick = onSignInSuccess,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Continue to Demo", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Demo build: Continue enters a local demo without an account. No sign-in or Firebase Auth is used in this build.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

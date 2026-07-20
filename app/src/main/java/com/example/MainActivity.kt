package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.viewmodel.AppViewModel
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: AppViewModel = viewModel()
      val isHighContrast = viewModel.isHighContrast.collectAsState().value
      val isLargeFont = viewModel.isLargeFont.collectAsState().value
      val isDarkMode = viewModel.isDarkMode.collectAsState().value
      val darkTheme = isDarkMode ?: androidx.compose.foundation.isSystemInDarkTheme()
      
      MyApplicationTheme(darkTheme = darkTheme, highContrast = isHighContrast, largeFont = isLargeFont) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
            AppNavigation(viewModel)
          }
        }
      }
    }
  }
}

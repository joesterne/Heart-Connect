package com.example.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AICounselingScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.EducationalScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.PrivateChatScreen
import com.example.viewmodel.AppViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: AppViewModel = viewModel()
    val context = LocalContext.current

    LaunchedEffect(viewModel.notificationEvent) {
        viewModel.notificationEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            AuthScreen(
                onSignInSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToCounseling = { navController.navigate("counseling") },
                onNavigateToCommunity = { navController.navigate("community") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToEducation = { navController.navigate("education") },
                onNavigateToFavorites = { navController.navigate("favorites") },
                onNavigateToPrivateChat = { peerId -> navController.navigate("private_chat/$peerId") }
            )
        }
        composable("counseling") {
            AICounselingScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("community") {
            CommunityScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("profile") {
            ProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("education") {
            EducationalScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("favorites") {
            FavoritesScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("private_chat/{peerId}") { backStackEntry ->
            val peerId = backStackEntry.arguments?.getString("peerId") ?: ""
            PrivateChatScreen(
                viewModel = viewModel,
                peerId = peerId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

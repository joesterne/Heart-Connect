package com.example.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ui.screens.AICounselingScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.MetricsScreen
import com.example.ui.screens.EducationalScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.PrivateChatScreen
import com.example.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorHeart

val LocalOpenDrawer = compositionLocalOf<() -> Unit> { { } }

@Composable
fun AppNavigation(viewModel: AppViewModel = viewModel()) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(viewModel.notificationEvent) {
        viewModel.notificationEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val gesturesEnabled = currentRoute != "auth"
    val isGlobalLoading by viewModel.isGlobalLoading.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            if (gesturesEnabled) {
                ModalDrawerSheet {
                    Spacer(Modifier.height(16.dp))
                    Text("Heart Connect", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider()
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Dashboard") },
                        selected = currentRoute == "dashboard",
                        onClick = { scope.launch { drawerState.close() }; navController.navigate("dashboard") { popUpTo(0) } },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Group, contentDescription = null) },
                        label = { Text("Community") },
                        selected = currentRoute == "community",
                        onClick = { scope.launch { drawerState.close() }; navController.navigate("community") { popUpTo(0) } },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null) },
                        label = { Text("AI Counselor") },
                        selected = currentRoute == "counseling",
                        onClick = { scope.launch { drawerState.close() }; navController.navigate("counseling") { popUpTo(0) } },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
                        label = { Text("Education") },
                        selected = currentRoute == "education",
                        onClick = { scope.launch { drawerState.close() }; navController.navigate("education") { popUpTo(0) } },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                        label = { Text("Favorites") },
                        selected = currentRoute == "favorites",
                        onClick = { scope.launch { drawerState.close() }; navController.navigate("favorites") { popUpTo(0) } },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.MonitorHeart, contentDescription = null) },
                        label = { Text("Health Metrics") },
                        selected = currentRoute == "metrics",
                        onClick = { scope.launch { drawerState.close() }; navController.navigate("metrics") { popUpTo(0) } },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Profile") },
                        selected = currentRoute == "profile",
                        onClick = { scope.launch { drawerState.close() }; navController.navigate("profile") { popUpTo(0) } },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        CompositionLocalProvider(LocalOpenDrawer provides { scope.launch { drawerState.open() } }) {
            NavHost(
                navController = navController, 
                startDestination = "auth",
                enterTransition = { fadeIn(animationSpec = tween(300)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300)) }
            ) {
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
                        onNavigateToMetrics = { navController.navigate("metrics") },
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
                
                composable("metrics") {
                    MetricsScreen(
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
    }
    }
    if (isGlobalLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

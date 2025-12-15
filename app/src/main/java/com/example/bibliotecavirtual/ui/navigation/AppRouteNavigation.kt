package com.example.bibliotecavirtual.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.bibliotecavirtual.data.db.AppDatabase
import com.example.bibliotecavirtual.data.network.GoogleBooksService
import com.example.bibliotecavirtual.data.repository.LivroRepository
import com.example.bibliotecavirtual.ui.screens.*
import com.example.bibliotecavirtual.ui.viewmodel.*

@Composable
fun AppRootNavigation(
    authViewModel: AuthViewModel,
    database: AppDatabase,
    booksService: GoogleBooksService
) {
    val currentUserId by authViewModel.currentUserId.collectAsState(initial = null)
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(rootNavController, authViewModel)
        }

        composable("signup") {
            SignupScreen(rootNavController, authViewModel)
        }

        composable("app_host") { backStackEntry ->

            val userId = currentUserId

            if (userId == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@composable
            }

            val livroRepository = LivroRepository(
                livroDao = database.livroDao(),
                booksService = booksService,
                currentUserId = userId
            )

            val livroViewModel: LivroViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = LivroViewModelFactory(livroRepository, userId)
            )

            AppNavigation(
                rootNavController = rootNavController,
                viewModel = livroViewModel,
                authViewModel = authViewModel
            )
        }
    }

    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            rootNavController.navigate("app_host") {
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        } else {
            val route = rootNavController.currentDestination?.route
            if (route != "login" && route != "signup") {
                rootNavController.navigate("login") {
                    popUpTo(rootNavController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
}

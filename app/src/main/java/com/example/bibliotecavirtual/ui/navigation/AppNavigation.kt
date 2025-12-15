package com.example.bibliotecavirtual.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.bibliotecavirtual.ui.screens.*
import com.example.bibliotecavirtual.ui.viewmodel.AuthViewModel
import com.example.bibliotecavirtual.ui.viewmodel.LivroViewModel

@Composable
fun AppNavigation(
    rootNavController: NavHostController,
    viewModel: LivroViewModel,
    authViewModel: AuthViewModel
) {
    val appNavController = rememberNavController()

    NavHost(
        navController = appNavController,
        startDestination = "listagem"
    ) {

        composable("listagem") {
            LivroListScreen(
                appNavController,
                rootNavController,
                viewModel,
                authViewModel
            )
        }

        composable(
            "add_edit/{livroId}",
            arguments = listOf(
                navArgument("livroId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) {
            val livroId = it.arguments?.getInt("livroId") ?: 0
            AddEditScreen(appNavController, livroId, viewModel)
        }

        composable(
            "detail/{livroId}",
            arguments = listOf(
                navArgument("livroId") { type = NavType.IntType }
            )
        ) {
            val livroId = it.arguments?.getInt("livroId") ?: 0
            LivroDetailScreen(appNavController, livroId, viewModel)
        }

        composable("favoritos") {
            FavoritosScreen(appNavController, viewModel)
        }

        composable("search") {
            SearchScreen(appNavController, viewModel)
        }
    }
}

package com.example.bibliotecavirtual

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.bibliotecavirtual.data.db.AppDatabase
import com.example.bibliotecavirtual.data.network.GoogleBooksService
import com.example.bibliotecavirtual.data.network.RetrofitClient
import com.example.bibliotecavirtual.data.repository.AuthRepository
import com.example.bibliotecavirtual.ui.navigation.AppRootNavigation
import com.example.bibliotecavirtual.ui.theme.BibliotecaVirtualTheme
import com.example.bibliotecavirtual.ui.viewmodel.AuthViewModel
import com.example.bibliotecavirtual.ui.viewmodel.AuthViewModelFactory

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(application) }
    private val booksService: GoogleBooksService by lazy { RetrofitClient.service }
    private val authRepository by lazy { AuthRepository(database.usuarioDao()) }

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(authRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BibliotecaVirtualTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRootNavigation(
                        authViewModel = authViewModel,
                        database = database,
                        booksService = booksService
                    )
                }
            }
        }
    }
}

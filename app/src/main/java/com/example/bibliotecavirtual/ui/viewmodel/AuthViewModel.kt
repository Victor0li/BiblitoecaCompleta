package com.example.bibliotecavirtual.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliotecavirtual.data.Usuario
import com.example.bibliotecavirtual.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUserId: StateFlow<Long?> = authRepository.currentUserId
    val currentUser: StateFlow<Usuario?> = authRepository.currentUser

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) = viewModelScope.launch {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            _uiState.value = AuthUiState.Error("Preencha todos os campos.")
            return@launch
        }

        _uiState.value = AuthUiState.Loading

        try {
            authRepository.login(trimmedEmail, trimmedPassword)
            _uiState.value = AuthUiState.Success
        } catch (e: IllegalArgumentException) {
            _uiState.value = AuthUiState.Error("E-mail ou senha inválidos.")
        } catch (e: Exception) {
            _uiState.value = AuthUiState.Error("Erro inesperado: ${e.message}")
        }
    }

    fun register(nome: String, email: String, password: String) = viewModelScope.launch {
        val trimmedNome = nome.trim()
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedNome.isBlank() || trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            _uiState.value = AuthUiState.Error("Preencha todos os campos.")
            return@launch
        }

        if (trimmedPassword.length < 6) {
            _uiState.value = AuthUiState.Error("A senha deve ter pelo menos 6 caracteres.")
            return@launch
        }

        _uiState.value = AuthUiState.Loading

        try {
            authRepository.register(trimmedNome, trimmedEmail, trimmedPassword)
            _uiState.value = AuthUiState.Success
        } catch (e: IllegalStateException) {
            _uiState.value = AuthUiState.Error(e.message ?: "E-mail já cadastrado.")
        } catch (e: Exception) {
            _uiState.value = AuthUiState.Error("Erro ao cadastrar: ${e.message}")
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState.Idle
    }

    fun clearUiState() {
        _uiState.value = AuthUiState.Idle
    }
}

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

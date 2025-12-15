package com.example.bibliotecavirtual.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliotecavirtual.data.repository.AuthRepository
import com.example.bibliotecavirtual.data.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/**
 * ViewModel principal responsável pela lógica de autenticação (Login/Cadastro) e
 * gerenciamento do estado da sessão do usuário.
 */
class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    // --- ESTADO DA SESSÃO ---

    // ID do usuário logado (Long? = null se deslogado).
    val currentUserId: StateFlow<Long?> = authRepository.currentUserId

    // Objeto completo do usuário logado (usado para exibir nome na UI, etc.)
    val currentUser: StateFlow<Usuario?> = authRepository.currentUser

    // --- ESTADO DA UI (Login/Cadastro) ---

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // --- FUNÇÕES DE AUTENTICAÇÃO ---

    fun login(email: String, password: String) = viewModelScope.launch {
        _uiState.value = AuthUiState.Loading

        // CORREÇÃO CRÍTICA: Remover espaços em branco para garantir que o hash da senha corresponda
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            _uiState.value = AuthUiState.Error("Preencha todos os campos.")
            return@launch
        }

        try {
            authRepository.login(trimmedEmail, trimmedPassword)
            _uiState.value = AuthUiState.Success
        } catch (e: IllegalArgumentException) {
            // Este erro é lançado quando a senha não confere ou o usuário não é encontrado
            _uiState.value = AuthUiState.Error("E-mail ou senha inválidos.")
        } catch (e: Exception) {
            _uiState.value = AuthUiState.Error("Ocorreu um erro desconhecido: ${e.message}")
        }
    }

    fun register(nome: String, email: String, password: String) = viewModelScope.launch {
        _uiState.value = AuthUiState.Loading

        // CORREÇÃO CRÍTICA: Remover espaços em branco no cadastro também
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

        try {
            authRepository.register(trimmedNome, trimmedEmail, trimmedPassword)
            _uiState.value = AuthUiState.Success
        } catch (e: IllegalStateException) {
            _uiState.value = AuthUiState.Error(e.message ?: "Este e-mail já está cadastrado.")
        } catch (e: Exception) {
            _uiState.value = AuthUiState.Error("Erro ao cadastrar: ${e.message}")
        }
    }

    fun logout() {
        authRepository.logout()
    }

    // Usado pela UI para limpar mensagens de erro
    fun clearError() {
        _uiState.value = AuthUiState.Idle
    }
}

// Classe selada para gerenciar o estado da UI de Login/Cadastro
sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
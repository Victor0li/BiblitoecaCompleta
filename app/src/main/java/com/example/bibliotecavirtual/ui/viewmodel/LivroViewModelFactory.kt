package com.example.bibliotecavirtual.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bibliotecavirtual.data.repository.LivroRepository

class LivroViewModelFactory(
    private val repository: LivroRepository,
    private val userId: Long // NOVO: ID do usuário logado
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LivroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Passa o Repository e o ID do usuário para o construtor do LivroViewModel
            return LivroViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
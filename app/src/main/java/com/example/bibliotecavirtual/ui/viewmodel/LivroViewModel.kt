package com.example.bibliotecavirtual.ui.viewmodel

import androidx.lifecycle.*
import com.example.bibliotecavirtual.data.Livro
import com.example.bibliotecavirtual.data.repository.LivroRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LivroViewModel(
    private val repository: LivroRepository,
    val currentUserId: Long
) : ViewModel() {

    private val _selectedLivro = MutableStateFlow<Livro?>(null)
    val selectedLivro: StateFlow<Livro?> = _selectedLivro.asStateFlow()

    fun getLivroById(id: Int) {
        if (id == 0) {
            _selectedLivro.value = null
            return
        }

        viewModelScope.launch {
            _selectedLivro.value = repository.getLivroById(id).firstOrNull()
        }
    }

    fun clearSelectedLivro() {
        _selectedLivro.value = null
    }

    val allLivros: LiveData<List<Livro>> = repository.allLivros.asLiveData()
    val favoritos: LiveData<List<Livro>> = repository.favoritos.asLiveData()

    fun inserir(livro: Livro) = viewModelScope.launch {
        repository.inserir(livro)
    }

    fun deletar(livro: Livro) = viewModelScope.launch {
        repository.deletar(livro)
    }

    fun atualizar(livro: Livro) = viewModelScope.launch {
        repository.atualizar(livro)
        if (_selectedLivro.value?.id == livro.id) {
            _selectedLivro.value = livro
        }
    }

    fun toggleFavorito(livro: Livro) = viewModelScope.launch {
        val updated = livro.copy(isFavorito = !livro.isFavorito)
        repository.atualizar(updated)
        if (_selectedLivro.value?.id == updated.id) {
            _selectedLivro.value = updated
        }
    }

    fun toggleLido(livro: Livro) = viewModelScope.launch {
        val updated = livro.copy(isLido = !livro.isLido)
        repository.atualizar(updated)
        if (_selectedLivro.value?.id == updated.id) {
            _selectedLivro.value = updated
        }
    }

    private val _searchedLivro = MutableStateFlow<Livro?>(null)
    val searchedLivro: StateFlow<Livro?> = _searchedLivro.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchErrorMessage = MutableStateFlow<String?>(null)
    val searchErrorMessage: StateFlow<String?> = _searchErrorMessage.asStateFlow()

    fun searchBook(isbn: String) = viewModelScope.launch {
        _searchedLivro.value = null
        _searchErrorMessage.value = null
        _isSearching.value = true

        try {
            val livro = repository.searchBookByIsbn(isbn)
            if (livro != null) {
                _searchedLivro.value = livro
            } else {
                _searchErrorMessage.value = "Livro não encontrado com o ISBN: $isbn"
            }
        } catch (e: Exception) {
            _searchErrorMessage.value = "Erro de rede. Verifique sua conexão."
        } finally {
            _isSearching.value = false
        }
    }

    fun clearSearchState() {
        _searchedLivro.value = null
        _searchErrorMessage.value = null
        _isSearching.value = false
    }
}

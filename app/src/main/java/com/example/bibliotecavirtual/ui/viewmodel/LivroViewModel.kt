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

    // --- ESTADO PARA A TELA DE DETALHES (CORREÇÃO PRINCIPAL) ---

    // StateFlow privado para guardar o livro que está sendo visualizado nos detalhes.
    private val _selectedLivro = MutableStateFlow<Livro?>(null)
    // StateFlow público e somente leitura para a UI observar.
    val selectedLivro: StateFlow<Livro?> = _selectedLivro.asStateFlow()

    /**
     * Busca um livro pelo seu ID no repositório e atualiza o _selectedLivro.
     * A tela de detalhes chamará esta função.
     */
    fun getLivroById(id: Int) {
        if (id == 0) {
            _selectedLivro.value = null
            return
        }
        viewModelScope.launch {
            // Usa .firstOrNull() para pegar o primeiro resultado do Flow do Room
            // e evitar que a tela fique escutando mudanças desnecessárias aqui.
            val livro = repository.getLivroById(id).firstOrNull()
            _selectedLivro.value = livro
        }
    }

    /**
     * Limpa o estado do livro selecionado.
     * Essencial para ser chamado quando o usuário sai da tela de detalhes.
     */
    fun clearSelectedLivro() {
        _selectedLivro.value = null
    }

    // --- FIM DA CORREÇÃO PRINCIPAL ---


    // --- DADOS PARA A LISTA PRINCIPAL (EXISTENTES) ---
    val allLivros: LiveData<List<Livro>> = repository.allLivros.asLiveData()
    val favoritos: LiveData<List<Livro>> = repository.favoritos.asLiveData()

    // --- FUNÇÕES DE AÇÃO (CRUD) ---

    fun inserir(livro: Livro) = viewModelScope.launch {
        repository.inserir(livro)
    }

    fun deletar(livro: Livro) = viewModelScope.launch {
        repository.deletar(livro)
    }

    fun atualizar(livro: Livro) = viewModelScope.launch {
        repository.atualizar(livro)
    }

    // Estas funções agora precisam atualizar o _selectedLivro se o livro modificado
    // for o que está sendo exibido na tela de detalhes.
    fun toggleFavorito(livro: Livro) = viewModelScope.launch {
        val updatedLivro = livro.copy(isFavorito = !livro.isFavorito)
        repository.atualizar(updatedLivro)
        // Atualiza o estado na tela de detalhes em tempo real
        if (_selectedLivro.value?.id == updatedLivro.id) {
            _selectedLivro.value = updatedLivro
        }
    }

    fun toggleLido(livro: Livro) = viewModelScope.launch {
        val updatedLivro = livro.copy(isLido = !livro.isLido)
        repository.atualizar(updatedLivro)
        // Atualiza o estado na tela de detalhes em tempo real
        if (_selectedLivro.value?.id == updatedLivro.id) {
            _selectedLivro.value = updatedLivro
        }
    }

    // --- CÓDIGO DE PESQUISA (EXISTENTE) ---
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
            val livroEncontrado = repository.searchBookByIsbn(isbn)
            if (livroEncontrado != null) {
                _searchedLivro.value = livroEncontrado
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

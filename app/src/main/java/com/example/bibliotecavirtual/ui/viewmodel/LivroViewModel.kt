package com.example.bibliotecavirtual.ui.viewmodel

import androidx.lifecycle.*
import com.example.bibliotecavirtual.data.Livro
import com.example.bibliotecavirtual.data.repository.LivroRepository
import kotlinx.coroutines.launch

class LivroViewModel(private val repository: LivroRepository) : ViewModel() {

    val allLivros: LiveData<List<Livro>> = repository.allLivros.asLiveData()
    val favoritos: LiveData<List<Livro>> = repository.favoritos.asLiveData()
    val lidos: LiveData<List<Livro>> = repository.lidos.asLiveData()
    val paraLer: LiveData<List<Livro>> = repository.paraLer.asLiveData()

    fun getLivroById(id: Int): LiveData<Livro?> {
        if (id == 0) return MutableLiveData(null)
        return repository.getLivroById(id).asLiveData()
    }

    fun inserir(livro: Livro) = viewModelScope.launch {
        repository.inserir(livro)
    }

    fun deletar(livro: Livro) = viewModelScope.launch {
        repository.deletar(livro)
    }

    fun atualizar(livro: Livro) = viewModelScope.launch {
        repository.atualizar(livro)
    }

    fun toggleFavorito(livro: Livro) = viewModelScope.launch {
        repository.toggleFavorito(livro)
    }

    fun toggleLido(livro: Livro) = viewModelScope.launch {
        // Chamando o método do Repository para mudar o status
        repository.toggleLido(livro)
    }
}
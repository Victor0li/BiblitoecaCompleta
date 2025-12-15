package com.example.bibliotecavirtual.data.repository

import com.example.bibliotecavirtual.data.Livro
import com.example.bibliotecavirtual.data.db.LivroDao
import com.example.bibliotecavirtual.data.network.GoogleBooksService
import com.example.bibliotecavirtual.data.network.Item
import kotlinx.coroutines.flow.Flow

class LivroRepository(
    private val livroDao: LivroDao,
    private val booksService: GoogleBooksService,
    private val currentUserId: Long
) {

    val allLivros: Flow<List<Livro>> = livroDao.getAll(currentUserId)
    val favoritos: Flow<List<Livro>> = livroDao.getFavorites(currentUserId)
    val lidos: Flow<List<Livro>> = livroDao.getLidos(currentUserId)
    val paraLer: Flow<List<Livro>> = livroDao.getParaLer(currentUserId)

    fun getLivroById(id: Int): Flow<Livro?> =
        livroDao.getLivroById(id, currentUserId)

    suspend fun inserir(livro: Livro) {
        require(livro.usuarioId == currentUserId)
        livroDao.inserir(livro)
    }

    suspend fun deletar(livro: Livro) {
        require(livro.usuarioId == currentUserId)
        livroDao.deletar(livro)
    }

    suspend fun atualizar(livro: Livro) {
        require(livro.usuarioId == currentUserId)
        livroDao.atualizar(livro)
    }

    suspend fun toggleFavorito(livro: Livro) {
        require(livro.usuarioId == currentUserId)
        livroDao.atualizar(livro.copy(isFavorito = !livro.isFavorito))
    }

    suspend fun toggleLido(livro: Livro) {
        require(livro.usuarioId == currentUserId)
        livroDao.atualizar(livro.copy(isLido = !livro.isLido))
    }

    private fun mapApiItemToLivro(item: Item): Livro {
        val volumeInfo = item.volumeInfo

        val genre = volumeInfo.categories?.firstOrNull() ?: "Não especificado"
        val ano = volumeInfo.anoPublicacao?.substringBefore('-')?.toIntOrNull() ?: 0
        val imageUrl = volumeInfo.imageLinks?.thumbnail

        return Livro(
            id = 0,
            usuarioId = currentUserId,
            titulo = volumeInfo.title ?: "Título Desconhecido",
            autor = volumeInfo.authors?.joinToString(", ") ?: "Autor Desconhecido",
            genre = genre,
            anoPublicacao = ano,
            description = volumeInfo.description ?: "Sem descrição disponível.",
            isFavorito = false,
            isLido = false,
            imageUrl = imageUrl
        )
    }

    suspend fun searchBookByIsbn(isbn: String): Livro? {
        return try {
            val response = booksService.searchBooks("isbn:$isbn")
            if (response.totalItems > 0 && !response.items.isNullOrEmpty()) {
                mapApiItemToLivro(response.items.first())
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

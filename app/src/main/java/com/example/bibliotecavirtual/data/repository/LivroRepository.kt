package com.example.bibliotecavirtual.data.repository

import com.example.bibliotecavirtual.data.Livro
import com.example.bibliotecavirtual.data.db.LivroDao
import com.example.bibliotecavirtual.data.network.GoogleBooksService
import com.example.bibliotecavirtual.data.network.Item
import kotlinx.coroutines.flow.Flow

// ATUALIZADO: O Repository agora recebe o ID do usuário logado
class LivroRepository(
    private val livroDao: LivroDao,
    private val booksService: GoogleBooksService,
    private val currentUserId: Long // NOVO: ID do usuário logado no momento
) {

    // --- FUNÇÕES DE PERSISTÊNCIA (ROOM) ---

    // Todas as chamadas ao DAO DEVEM incluir o currentUserId
    val allLivros: Flow<List<Livro>> = livroDao.getAll(currentUserId)
    val favoritos: Flow<List<Livro>> = livroDao.getFavorites(currentUserId)
    val lidos: Flow<List<Livro>> = livroDao.getLidos(currentUserId)
    val paraLer: Flow<List<Livro>> = livroDao.getParaLer(currentUserId)

    fun getLivroById(id: Int): Flow<Livro?> = livroDao.getLivroById(id, currentUserId)

    // Nota: O objeto Livro passado para inserção/atualização/deleção JÁ DEVE conter o currentUserId.

    suspend fun inserir(livro: Livro) {
        // Validação: Garante que o livro está sendo inserido com o ID do usuário correto
        require(livro.usuarioId == currentUserId) { "O livro deve pertencer ao usuário logado." }
        livroDao.inserir(livro)
    }

    suspend fun deletar(livro: Livro) {
        require(livro.usuarioId == currentUserId) { "O livro deve pertencer ao usuário logado." }
        livroDao.deletar(livro)
    }

    suspend fun atualizar(livro: Livro) {
        require(livro.usuarioId == currentUserId) { "O livro deve pertencer ao usuário logado." }
        livroDao.atualizar(livro)
    }

    suspend fun toggleFavorito(livro: Livro) {
        require(livro.usuarioId == currentUserId) { "O livro deve pertencer ao usuário logado." }
        val updatedLivro = livro.copy(isFavorito = !livro.isFavorito)
        livroDao.atualizar(updatedLivro)
    }

    suspend fun toggleLido(livro: Livro) {
        require(livro.usuarioId == currentUserId) { "O livro deve pertencer ao usuário logado." }
        val updatedLivro = livro.copy(isLido = !livro.isLido)
        livroDao.atualizar(updatedLivro)
    }

    // --- FUNÇÕES DE BUSCA EXTERNA (API GOOGLE BOOKS) ---

    /**
     * Mapeia o item da API do Google Books para a entidade Livro local.
     * ADICIONA O currentUserId OBRIGATORIAMENTE.
     */
    private fun mapApiItemToLivro(item: Item): Livro {
        val volumeInfo = item.volumeInfo

        val genre = volumeInfo.categories?.firstOrNull() ?: "Não especificado"
        val ano = volumeInfo.anoPublicacao?.substringBefore('-')?.toIntOrNull() ?: 0
        val imageUrl = item.volumeInfo.imageLinks?.thumbnail

        // ATUALIZADO: Agora injeta o ID do usuário logado
        return Livro(
            id = 0,
            usuarioId = currentUserId, // <--- CAMPO ESSENCIAL ADICIONADO AQUI
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

    /**
     * Busca um livro na API do Google Books usando o ISBN.
     * @return Livro? Mapeado para a entidade local, ou null se não for encontrado.
     */
    suspend fun searchBookByIsbn(isbn: String): Livro? {
        val query = "isbn:$isbn"

        return try {
            val response = booksService.searchBooks(query)

            if (response.totalItems > 0 && !response.items.isNullOrEmpty()) {
                mapApiItemToLivro(response.items.first())
            } else {
                null
            }
        } catch (e: Exception) {
            println("Erro ao buscar livro na API: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
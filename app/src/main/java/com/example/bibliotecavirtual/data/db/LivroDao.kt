package com.example.bibliotecavirtual.data.db

import androidx.room.*
import com.example.bibliotecavirtual.data.Livro
import kotlinx.coroutines.flow.Flow

@Dao
interface LivroDao {

    // --- CONSULTAS (TODAS FILTRADAS POR usuarioId) ---

    // Retorna todos os livros do usuário logado
    @Query("SELECT * FROM tabela_livros WHERE usuarioId = :usuarioId ORDER BY titulo ASC")
    fun getAll(usuarioId: Long): Flow<List<Livro>>

    // Retorna apenas os favoritos do usuário
    @Query("SELECT * FROM tabela_livros WHERE usuarioId = :usuarioId AND isFavorito = 1 ORDER BY titulo ASC")
    fun getFavorites(usuarioId: Long): Flow<List<Livro>>

    // Retorna um livro específico do usuário (requer tanto o ID do livro quanto o ID do usuário)
    @Query("SELECT * FROM tabela_livros WHERE id = :livroId AND usuarioId = :usuarioId")
    fun getLivroById(livroId: Int, usuarioId: Long): Flow<Livro?>

    // Retorna os livros já lidos do usuário
    @Query("SELECT * FROM tabela_livros WHERE usuarioId = :usuarioId AND isLido = 1 ORDER BY titulo ASC")
    fun getLidos(usuarioId: Long): Flow<List<Livro>>

    // Retorna os livros para ler do usuário
    @Query("SELECT * FROM tabela_livros WHERE usuarioId = :usuarioId AND isLido = 0 ORDER BY titulo ASC")
    fun getParaLer(usuarioId: Long): Flow<List<Livro>>


    // --- OPERAÇÕES DE ESCRITA (O Livro já contém o usuarioId) ---

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserir(livro: Livro)

    @Delete
    suspend fun deletar(livro: Livro)

    // Nota: O Room usa a Primary Key (id) para encontrar e atualizar.
    // O Livro que é passado aqui DEVE conter o usuarioId correto.
    @Update
    suspend fun atualizar(livro: Livro)
}
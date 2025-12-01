package com.example.bibliotecavirtual.data.db

import androidx.room.*
import com.example.bibliotecavirtual.data.Livro
import kotlinx.coroutines.flow.Flow

@Dao
interface LivroDao {

    @Query("SELECT * FROM tabela_livros ORDER BY titulo ASC")
    fun getAll(): Flow<List<Livro>>

    @Query("SELECT * FROM tabela_livros WHERE isFavorito = 1 ORDER BY titulo ASC")
    fun getFavorites(): Flow<List<Livro>>

    @Query("SELECT * FROM tabela_livros WHERE id = :livroId")
    fun getLivroById(livroId: Int): Flow<Livro?>

    @Query("SELECT * FROM tabela_livros WHERE isLido = 1 ORDER BY titulo ASC")
    fun getLidos(): Flow<List<Livro>>

    @Query("SELECT * FROM tabela_livros WHERE isLido = 0 ORDER BY titulo ASC")
    fun getParaLer(): Flow<List<Livro>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserir(livro: Livro)

    @Delete
    suspend fun deletar(livro: Livro)

    @Update
    suspend fun atualizar(livro: Livro)
}
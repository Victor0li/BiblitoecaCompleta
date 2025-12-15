package com.example.bibliotecavirtual.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bibliotecavirtual.data.Usuario

@Dao
interface UsuarioDao {

    @Insert
    suspend fun inserir(usuario: Usuario): Long // Retorna o ID do usuário inserido

    // Usado para o login: encontrar o usuário pelo email
    @Query("SELECT * FROM tabela_usuarios WHERE email = :email LIMIT 1")
    suspend fun getUsuarioByEmail(email: String): Usuario?

    // Usado para verificar se o email já existe (cadastro)
    @Query("SELECT COUNT(id) FROM tabela_usuarios WHERE email = :email")
    suspend fun countByEmail(email: String): Int

    // Usado para buscar o usuário logado (opcional, mas útil)
    @Query("SELECT * FROM tabela_usuarios WHERE id = :id LIMIT 1")
    suspend fun getUsuarioById(id: Long): Usuario?
}
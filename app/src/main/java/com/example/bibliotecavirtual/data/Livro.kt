package com.example.bibliotecavirtual.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabela_livros")
data class Livro(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val autor: String,
    val genre: String,
    val anoPublicacao: Int,
    val description: String,
    val isFavorito: Boolean = false,
    val isLido: Boolean = false
)
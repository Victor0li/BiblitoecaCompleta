package com.example.bibliotecavirtual.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "tabela_livros",
    foreignKeys = [ForeignKey(
        entity = Usuario::class,
        parentColumns = ["id"],
        childColumns = ["usuarioId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Livro(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Long,
    val titulo: String,
    val autor: String,
    val genre: String,
    val anoPublicacao: Int,
    val description: String,
    val isFavorito: Boolean = false,
    val isLido: Boolean = false,
    val imageUrl: String? = null
)

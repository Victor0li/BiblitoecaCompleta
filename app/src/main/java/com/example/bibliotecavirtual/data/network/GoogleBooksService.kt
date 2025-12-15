package com.example.bibliotecavirtual.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val description: String?,
    @SerializedName("publishedDate")
    val anoPublicacao: String?,
    val categories: List<String>?,
    val imageLinks: ImageLinks?
)

data class Item(
    val volumeInfo: VolumeInfo
)

data class BooksResponse(
    val totalItems: Int,
    val items: List<Item>?
)

data class ImageLinks(
    val thumbnail: String?
)

// Retrofit
interface GoogleBooksService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String
    ): BooksResponse
}
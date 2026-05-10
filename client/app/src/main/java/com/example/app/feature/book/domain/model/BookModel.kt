package com.example.app.feature.book.domain.model

data class BookModel(
    val id: Int,
    val title: String,
    val description: String,
    val author: String,
    val year: Int,
    val fileUrl: String,
    val coverUrl: String?,
    val userId: Int
)

package com.example.model

data class Book(
    val id: Int,
    val title: String,
    val description: String,
    val author: String,
    val year: Int,
    val fileUrl: String,
    val coverUrl: String?,
    val userId: Int
)

package com.example.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class BookResponse(
    val id: Int,
    val title: String,
    val description: String?,
    val author: String,
    val year: Int,
    val fileUrl: String,
    val coverUrl: String?,
    val userId: Int
)

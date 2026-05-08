package com.example.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateBookRequest(
    val title: String,
    val description: String? = null,
    val author: String,
    val year: Int
)

package com.example.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val userId: Int,
    val email: String,
    val name: String
)

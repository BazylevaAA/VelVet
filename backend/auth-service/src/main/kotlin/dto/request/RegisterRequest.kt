package com.example.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
)

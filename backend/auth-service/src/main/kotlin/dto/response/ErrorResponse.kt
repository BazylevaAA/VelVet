package com.example.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String?
)

package com.example.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateTrackRequest(
    val title: String,
    val artist: String,
    val album: String? = null,
    val duration: Int
)

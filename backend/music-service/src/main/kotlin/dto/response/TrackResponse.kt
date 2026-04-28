package com.example.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class TrackResponse(
    val id: Int,
    val title: String,
    val artist: String,
    val album: String?,
    val duration: Int,
    val fileUrl: String,
    val coverUrl: String?,
    val userId: Int,
)
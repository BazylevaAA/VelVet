package com.example.app.feature.music.domain.model

data class TrackModel(
    val id: Int,
    val title: String,
    val artist: String,
    val album: String?,
    val duration: Int,
    val fileUrl: String,
    val coverUrl: String?,
    val userId: Int
)

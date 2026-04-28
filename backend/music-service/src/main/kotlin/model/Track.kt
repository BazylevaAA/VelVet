package com.example.model

data class Track(
    val id: Int,
    val title: String,
    val artist: String,
    val album: String?,
    val duration: Int,
    val fileUrl: String,
    val coverUrl: String?,
    val userId: Int
)

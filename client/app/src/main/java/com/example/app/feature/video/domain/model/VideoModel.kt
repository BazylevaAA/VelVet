package com.example.app.feature.video.domain.model

data class VideoModel(
    val id: Int,
    val title: String,
    val description: String?,
    val director: String?,
    val year: Int,
    val duration: Int,
    val genre: String?,
    val fileUrl: String,
    val coverUrl: String?,
    val userId: Int
)

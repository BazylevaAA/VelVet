package com.example.app.feature.video.domain.usecase

import com.example.app.feature.video.data.VideoRepository

class GetVideoUseCase (private val repository: VideoRepository) {
    suspend operator fun invoke() = repository.getAllVideos()
}
package com.example.app.feature.video.domain.usecase

import com.example.app.feature.video.data.VideoRepository

class DeleteVideoUseCase(private val repository: VideoRepository) {
    suspend operator fun invoke(id: Int) = repository.deleteVideo(id)
}
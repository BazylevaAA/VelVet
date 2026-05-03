package com.example.app.feature.music.domain.usecase

import com.example.app.feature.music.data.MusicRepository

class DeleteTrackUseCase(private val repository: MusicRepository) {

    suspend operator fun invoke(id: Int): Result<Unit> {
        return repository.deleteTrack(id)
    }
}
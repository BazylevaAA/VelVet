package com.example.app.feature.music.domain.usecase

import com.example.app.feature.music.data.MusicRepository
import com.example.app.feature.music.domain.model.TrackModel

class GetTracksUseCase(private val repository: MusicRepository) {

    suspend operator fun invoke(): Result<List<TrackModel>>{
        return repository.getAllTracks()
    }
}
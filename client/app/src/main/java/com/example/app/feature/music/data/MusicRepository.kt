package com.example.app.feature.music.data

import com.example.app.feature.music.domain.model.TrackModel

class MusicRepository(private val musicApi: MusicApi){

    suspend fun getAllTracks(): Result<List<TrackModel>>{
        return try{
            val response = musicApi.getAllTracks().map { it.toModel() }
            Result.success(response)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getTrackById(id: Int): Result<TrackModel> {
        return try {
            val response = musicApi.getTrackById(id).toModel()
            Result.success(response)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun deleteTrack(id: Int): Result<Unit> {
        return try {
            musicApi.deleteTrack(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun TrackDto.toModel() = TrackModel(
        id       = id,
        title    = title,
        artist   = artist,
        album    = album,
        duration = duration,
        fileUrl  = fileUrl,
        coverUrl = coverUrl,
        userId   = userId
    )
}
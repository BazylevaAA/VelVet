package com.example.app.feature.video.data

import com.example.app.feature.video.domain.model.VideoModel

class VideoRepository (private val videoApi: VideoApi) {

    suspend fun getAllVideos(): Result<List<VideoModel>>{
        return try {
            val response = videoApi.getAllVideos().map{ it.toModel() }
            Result.success(response)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun deleteVideo(id: Int): Result<Unit> {
        return try {
            videoApi.deleteVideo(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun VideoDto.toModel() = VideoModel(
        id          = id,
        title       = title,
        description = description,
        director    = director,
        year        = year,
        duration    = duration,
        genre       = genre,
        fileUrl     = fileUrl,
        coverUrl    = coverUrl,
        userId      = userId
    )
}
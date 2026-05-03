package com.example.app.feature.video.data

import com.example.app.core.network.BASE_URL
import com.example.app.core.storage.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

@Serializable
data class VideoDto(
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

class VideoApi(private val client: HttpClient, private val tokenStorage: TokenStorage) {

    private suspend fun getToken(): String = tokenStorage.token.first() ?: ""

    suspend fun getAllVideos(): List<VideoDto> {
        return client.get("$BASE_URL:8083/api/v1/videos") {
            bearerAuth(getToken())
        }.body()
    }

    suspend fun getVideoById(id: Int): VideoDto {
        return client.get("$BASE_URL:8083/api/v1/videos/$id") {
            bearerAuth(getToken())
        }.body()
    }

    suspend fun deleteVideo(id: Int) {
        client.delete("$BASE_URL:8083/api/v1/videos/$id") {
            bearerAuth(getToken())
        }
    }
}
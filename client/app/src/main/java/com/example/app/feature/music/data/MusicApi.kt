package com.example.app.feature.music.data

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
data class TrackDto(
    val id: Int,
    val title: String,
    val artist: String,
    val album: String?,
    val duration: Int,
    val fileUrl: String,
    val coverUrl: String?,
    val userId: Int
)

class MusicApi(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage
) {

    private suspend fun getToken(): String = tokenStorage.token.first() ?: ""

    suspend fun getAllTracks(): List<TrackDto>{
        return client.get("$BASE_URL:8082/api/v1/tracks"){
            bearerAuth(getToken())
        }.body()
    }

    suspend fun getTrackById(id: Int): TrackDto {
        return client.get("$BASE_URL:8082/api/v1/tracks/$id") {
            bearerAuth(getToken())
        }.body()
    }

    suspend fun deleteTrack(id: Int) {
        client.delete("$BASE_URL:8082/api/v1/tracks/$id") {
            bearerAuth(getToken())
        }
    }
}
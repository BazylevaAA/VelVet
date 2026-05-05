package com.example.service

import com.example.dto.request.CreateTrackRequest
import com.example.dto.response.TrackResponse
import com.example.exception.MusicException
import com.example.model.Track
import com.example.repository.TrackRepository
import com.example.storage.MinioStorage
import io.ktor.http.*
import java.io.InputStream
import java.util.UUID

class MusicService(
    private val trackRepository: TrackRepository
) {

    suspend fun uploadTrack(
        request: CreateTrackRequest,
        audioStream: InputStream,
        audioSize: Long,
        audioContentType: String,
        coverStream: InputStream?,
        coverSize: Long?,
        coverContentType: String?,
        userId: Int
    ): TrackResponse {

        if (request.title.isBlank()) {
            throw MusicException(HttpStatusCode.BadRequest, "Title cannot be empty")
        }
        if (request.artist.isBlank()) {
            throw MusicException(HttpStatusCode.BadRequest, "Artist cannot be empty")
        }
        if (request.duration <= 0) {
            throw MusicException(HttpStatusCode.BadRequest, "Duration must be positive")
        }

        val audioKey = "tracks/${UUID.randomUUID()}.${getExtension(audioContentType)}"

        val fileUrl = MinioStorage.uploadFile(
            key         = audioKey,
            inputStream = audioStream,
            contentType = audioContentType,
            size        = audioSize
        )

        val coverUrl = if (coverStream != null && coverSize != null && coverContentType != null) {
            val coverKey = "covers/${UUID.randomUUID()}.${getExtension(coverContentType)}"
            MinioStorage.uploadFile(
                key         = coverKey,
                inputStream = coverStream,
                contentType = coverContentType,
                size        = coverSize
            )
        } else null

        val track = trackRepository.create(
            title    = request.title,
            artist   = request.artist,
            album    = request.album,
            duration = request.duration,
            fileUrl  = fileUrl,
            coverUrl = coverUrl,
            userId   = userId
        )

        return toResponse(track)
    }

    suspend fun getAllTracks(userId: Int): List<TrackResponse> {
        return trackRepository.finadAllByUserId(userId).map { toResponse(it) }
    }

    suspend fun getTrackById(id: Int, userId: Int): TrackResponse {
        val track = trackRepository.findById(id)
            ?: throw MusicException(HttpStatusCode.NotFound, "Track not found")

        if (track.userId != userId) {
            throw MusicException(HttpStatusCode.Forbidden, "Access denied")
        }

        return toResponse(track)
    }

    suspend fun deleteTrack(id: Int, userId: Int) {
        val track = trackRepository.findById(id)
            ?: throw MusicException(HttpStatusCode.NotFound, "Track not found")

        if (track.userId != userId) {
            throw MusicException(HttpStatusCode.Forbidden, "Access denied")
        }

        val audioKey = track.fileUrl.substringAfterLast("velvet-music/")
        MinioStorage.deleteFile(audioKey)

        track.coverUrl?.let { url ->
            val coverKey = url.substringAfterLast("velvet-music/")
            MinioStorage.deleteFile(coverKey)
        }

        trackRepository.delete(id)
    }

    suspend fun streamTrack(id: Int): InputStream {
        val track = trackRepository.findById(id)
            ?: throw MusicException(HttpStatusCode.NotFound, "Track not found")
        val key = track.fileUrl.substringAfter("velvet-music/")
        return MinioStorage.getFile(key)
    }

    private fun getExtension(contentType: String): String {
        return when (contentType) {
            "audio/mpeg"  -> "mp3"
            "audio/wav"   -> "wav"
            "audio/ogg"   -> "ogg"
            "audio/flac"  -> "flac"
            "image/jpeg"  -> "jpg"
            "image/png"   -> "png"
            "image/webp"  -> "webp"
            else          -> "bin"
        }
    }

    private fun toResponse(track: Track) = TrackResponse(
        id       = track.id,
        title    = track.title,
        artist   = track.artist,
        album    = track.album,
        duration = track.duration,
        fileUrl  = track.fileUrl,
        coverUrl = track.coverUrl,
        userId   = track.userId
    )
}
package service

import dto.request.CreateVideoRequest
import dto.response.VideoResponse
import exception.VideoException
import io.ktor.http.HttpStatusCode
import model.Video
import repository.VideoRepository
import storage.MinioStorage
import java.io.InputStream
import java.util.UUID

class VideoService(
    private val videoRepository: VideoRepository
) {

    suspend fun uploadVideo(
        request: CreateVideoRequest,
        videoStream: InputStream,
        videoSize: Long,
        videoContentType: String,
        coverStream: InputStream?,
        coverSize: Long?,
        coverContentType: String?,
        userId: Int
    ): VideoResponse {

        if (request.title.isBlank()) {
            throw VideoException(HttpStatusCode.BadRequest, "Title cannot be empty")
        }
        if (request.year < 1888 || request.year > 2100) {
            throw VideoException(HttpStatusCode.BadRequest, "Invalid year")
        }
        if (request.duration <= 0) {
            throw VideoException(HttpStatusCode.BadRequest, "Duration must be positive")
        }

        val videoKey = "videos/${UUID.randomUUID()}.${getExtension(videoContentType)}"

        val fileUrl = MinioStorage.uploadFile(
            key         = videoKey,
            inputStream = videoStream,
            contentType = videoContentType,
            size        = videoSize
        )

        val coverUrl = if (coverStream != null && coverSize != null && coverContentType != null) {
            val coverKey = "posters/${UUID.randomUUID()}.${getExtension(coverContentType)}"
            MinioStorage.uploadFile(
                key         = coverKey,
                inputStream = coverStream,
                contentType = coverContentType,
                size        = coverSize
            )
        } else null

        val video = videoRepository.create(
            title       = request.title,
            description = request.description,
            director    = request.director,
            year        = request.year,
            duration    = request.duration,
            genre       = request.genre,
            fileUrl     = fileUrl,
            coverUrl    = coverUrl,
            userId      = userId
        )

        return toResponse(video)
    }

    suspend fun getAllVideos(userId: Int): List<VideoResponse> {
        return videoRepository.findAllByUserId(userId).map { toResponse(it) }
    }

    suspend fun getVideoById(id: Int, userId: Int): VideoResponse {
        val video = videoRepository.findById(id)
            ?: throw VideoException(HttpStatusCode.NotFound, "Video not found")

        if (video.userId != userId) {
            throw VideoException(HttpStatusCode.Forbidden, "Access denied")
        }

        return toResponse(video)
    }

    suspend fun deleteVideo(id: Int, userId: Int) {
        val video = videoRepository.findById(id)
            ?: throw VideoException(HttpStatusCode.NotFound, "Video not found")

        if (video.userId != userId) {
            throw VideoException(HttpStatusCode.Forbidden, "Access denied")
        }

        // Удаляем файлы из MinIO
        val videoKey = video.fileUrl.substringAfterLast("velvet-movie/")
        MinioStorage.deleteFile(videoKey)

        video.coverUrl?.let { url ->
            val coverKey = url.substringAfterLast("velvet-movie/")
            MinioStorage.deleteFile(coverKey)
        }

        videoRepository.delete(id)
    }

    suspend fun streamVideo(id: Int): InputStream {
        val video = videoRepository.findById(id)
            ?: throw VideoException(HttpStatusCode.NotFound, "Video not found")
        val key = video.fileUrl.substringAfter("velvet-video/")
        return MinioStorage.getFile(key)
    }

    private fun getExtension(contentType: String): String {
        return when (contentType) {
            "video/mp4"       -> "mp4"
            "video/x-msvideo" -> "avi"
            "video/mkv"       -> "mkv"
            "video/webm"      -> "webm"
            "image/jpeg"      -> "jpg"
            "image/png"       -> "png"
            "image/webp"      -> "webp"
            else              -> "bin"
        }
    }

    private fun toResponse(video: Video) = VideoResponse(
        id = video.id,
        title = video.title,
        description = video.description,
        director = video.director,
        year = video.year,
        duration = video.duration,
        genre = video.genre,
        fileUrl = video.fileUrl,
        coverUrl = video.coverUrl,
        userId = video.userId
    )
}
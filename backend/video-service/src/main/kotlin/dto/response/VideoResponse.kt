package dto.response

import kotlinx.serialization.Serializable

@Serializable
data class VideoResponse(
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

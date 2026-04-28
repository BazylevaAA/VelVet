package dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateVideoRequest(
    val title: String,
    val description: String? = null,
    val director: String? = null,
    val year: Int,
    val duration: Int,
    val genre: String? = null
)

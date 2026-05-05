package plugins

import dto.request.CreateVideoRequest
import exception.VideoException
import service.VideoService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import repository.VideoRepository

fun Application.configureRouting() {
    val videoService = VideoService(VideoRepository())

    routing {
        route("/api/v1"){
        authenticate("auth-jwt") {

            post("/videos/upload") {
                val userId = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()

                val multipart = call.receiveMultipart(formFieldLimit = 2L * 1024 * 1024 * 1024)

                var request: CreateVideoRequest? = null
                var videoBytes: ByteArray? = null
                var videoContentType = "video/mp4"
                var coverBytes: ByteArray? = null
                var coverContentType: String? = null

                multipart.forEachPart { part ->
                    when {
                        part is PartData.FormItem && part.name == "data" -> {
                            request = Json.decodeFromString(part.value)
                        }
                        part is PartData.FileItem && part.name == "video" -> {
                            videoContentType = part.contentType?.toString() ?: "video/mp4"
                            videoBytes = part.streamProvider().readBytes()
                        }
                        part is PartData.FileItem && part.name == "cover" -> {
                            coverContentType = part.contentType?.toString()
                            coverBytes = part.streamProvider().readBytes()
                        }
                    }
                    part.dispose()
                }

                val req = request
                    ?: throw VideoException(HttpStatusCode.BadRequest, "Missing 'data' field")
                val video = videoBytes
                    ?: throw VideoException(HttpStatusCode.BadRequest, "Missing 'video' file")

                val response = videoService.uploadVideo(
                    request          = req,
                    videoStream      = video.inputStream(),
                    videoSize        = video.size.toLong(),
                    videoContentType = videoContentType,
                    coverStream      = coverBytes?.inputStream(),
                    coverSize        = coverBytes?.size?.toLong(),
                    coverContentType = coverContentType,
                    userId           = userId
                )

                call.respond(HttpStatusCode.Created, response)
            }

            get("/videos") {
                val userId = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()
                call.respond(videoService.getAllVideos(userId))
            }

            get("/videos/{id}") {
                val userId = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw VideoException(HttpStatusCode.BadRequest, "Invalid video id")
                call.respond(videoService.getVideoById(id, userId))
            }

            delete("/videos/{id}") {
                val userId = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw VideoException(HttpStatusCode.BadRequest, "Invalid video id")
                videoService.deleteVideo(id, userId)
                call.respond(HttpStatusCode.NoContent)
            }

            get("/videos/{id}/stream") {
                val userId = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw VideoException(HttpStatusCode.BadRequest, "Invalid video id")
                videoService.getVideoById(id, userId)
                val stream = videoService.streamVideo(id)
                call.respondOutputStream(ContentType.Video.MP4) {
                    stream.copyTo(this)
                    stream.close()
                }
            }
        }
    }
    }
}
package com.example.plugins

import com.example.dto.request.CreateTrackRequest
import com.example.exception.MusicException
import com.example.repository.TrackRepository
import com.example.service.MusicService
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondOutputStream
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun Application.configureRouting() {
    val musicService = MusicService(TrackRepository())

    routing {
        authenticate("auth-jwt") {
            post("/tracks/upload") {
                val userId = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()

                val multipart = call.receiveMultipart()

                var request: CreateTrackRequest? = null
                var audioBytes: ByteArray? = null
                var audioContentType = "audio/mpeg"
                var coverBytes: ByteArray? = null
                var coverContentType: String? = null

                multipart.forEachPart { part ->
                    when {
                        part is PartData.FormItem && part.name == "data" -> {
                            request = Json.decodeFromString(part.value)
                        }
                        part is PartData.FileItem && part.name == "audio" -> {
                            audioContentType = part.contentType?.toString() ?: "audio/mpeg"
                            audioBytes = part.streamProvider().readBytes()
                        }
                        part is PartData.FileItem && part.name == "cover" -> {
                            coverContentType = part.contentType?.toString()
                            coverBytes = part.streamProvider().readBytes()
                        }
                    }
                    part.dispose()
                }

                val req = request
                    ?: throw MusicException(HttpStatusCode.BadRequest, "Missing 'data' field")
                val audio = audioBytes
                    ?: throw MusicException(HttpStatusCode.BadRequest, "Missing 'audio' file")

                val response = musicService.uploadTrack(
                    request          = req,
                    audioStream      = audio.inputStream(),
                    audioSize        = audio.size.toLong(),
                    audioContentType = audioContentType,
                    coverStream      = coverBytes?.inputStream(),
                    coverSize        = coverBytes?.size?.toLong(),
                    coverContentType = coverContentType,
                    userId           = userId
                )

                call.respond(HttpStatusCode.Created, response)
            }

            get("/tracks") {
                val userId = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()
                call.respond(musicService.getAllTracks(userId))
            }

            get("/tracks/{id}") {
                val userId = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw MusicException(HttpStatusCode.BadRequest, "Invalid track id")
                call.respond(musicService.getTrackById(id, userId))
            }

            delete("/tracks/{id}") {
                val userId = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw MusicException(HttpStatusCode.BadRequest, "Invalid track id")
                musicService.deleteTrack(id, userId)
                call.respond(HttpStatusCode.NoContent)
            }

            get("/tracks/{id}/stream") {
                val userId = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw MusicException(HttpStatusCode.BadRequest, "Invalid track id")

                musicService.getTrackById(id, userId)

                val stream = musicService.streamTrack(id)
                call.respondOutputStream(ContentType.Audio.MPEG) {
                    stream.use { it.copyTo(this) }
                }
            }
        }
    }
}

package com.example.plugins

import com.example.exception.MusicException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val message: String)

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<MusicException> { call, cause ->
            call.respond(cause.statusCode, ErrorResponse(cause.message))
        }
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse(cause.message ?: "Internal server error"))
        }
    }
}

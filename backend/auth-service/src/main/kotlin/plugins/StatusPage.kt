package com.example.plugins

import com.example.dto.response.ErrorResponse
import com.example.exception.AuthException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.slf4j.LoggerFactory
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.exception
import io.ktor.server.response.respond

private val logger = LoggerFactory.getLogger("StatusPages")

fun Application.configureStatusPages(){
    install(StatusPages) {

        exception<AuthException> { call, cause -> call.respond(cause.statusCode, ErrorResponse(cause.message)) }

        exception<IllegalArgumentException> { call, cause -> call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message)) }

        exception<Throwable> { call, cause ->
            logger.error("Unhandled exception", cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Internal Server Error"))
        }
    }
}
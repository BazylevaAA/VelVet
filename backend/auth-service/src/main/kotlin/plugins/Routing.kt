package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.example.repository.UserRepository
import com.example.routes.authRoutes
import com.example.service.AuthService

fun Application.configureRouting(){
    val userRepository = UserRepository()
    val authService = AuthService(
        userRepository = userRepository,
        jwtSecret = environment.config.property("jwt.secret").getString(),
        jwtIssuer = environment.config.property("jwt.issuer").getString(),
        jwtAudience = environment.config.property("jwt.audience").getString(),
    )

    routing {
        route("/api/v1") {
            authRoutes(authService)
        }
    }
}
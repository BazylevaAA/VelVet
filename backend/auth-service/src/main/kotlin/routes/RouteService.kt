package com.example.routes

import com.example.dto.request.LoginRequest
import com.example.dto.request.RegisterRequest
import com.example.service.AuthService
import io.ktor.http.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(
    authService: AuthService
){
    route("/auth"){

        post("/register"){
            val request = call.receive<RegisterRequest>()
            val response = authService.register(request)
            call.respond(HttpStatusCode.Created, response)
        }

        post("/login"){
            val request = call.receive<LoginRequest>()
            val response = authService.login(request)
            call.respond(HttpStatusCode.OK, response)
        }
    }

    authenticate("auth-jwt"){
        get("/me"){
            val principal = call.principal<JWTPrincipal>()!!
            val userId = principal.payload.getClaim("userId").asInt()
            val email = principal.payload.getClaim("email").asString()

            call.respond(HttpStatusCode.OK, mapOf("userId" to userId.toString(), "email" to email))
        }
    }
}
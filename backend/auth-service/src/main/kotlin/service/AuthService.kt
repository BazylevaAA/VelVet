package com.example.service

import com.example.dto.request.LoginRequest
import com.example.dto.request.RegisterRequest
import com.example.dto.response.AuthResponse
import com.example.exception.AuthException
import com.example.repository.UserRepository
import com.example.utils.JwtUtils
import io.ktor.http.HttpStatusCode
import org.mindrot.jbcrypt.BCrypt

class AuthService(
    private val userRepository: UserRepository,
    private val jwtSecret: String,
    private val jwtIssuer: String,
    private val jwtAudience: String,
) {

    suspend fun register(request: RegisterRequest): AuthResponse {
        if (request.email.isBlank()) {
            throw AuthException(HttpStatusCode.BadRequest, "Email cannot be empty")
        }

        if (request.password.length < 6) {
            throw AuthException(HttpStatusCode.BadRequest, "Password must be at least 6 characters")
        }

        if (request.name.isBlank()) {
            throw AuthException(HttpStatusCode.BadRequest, "Name cannot be empty")
        }

        if (userRepository.existsByEmail(request.email)) {
            throw AuthException(HttpStatusCode.Conflict, "User already exists")
        }

        val passwordHash = BCrypt.hashpw(request.password, BCrypt.gensalt())

        val user = userRepository.create(request.email, passwordHash, request.name)

        val token = JwtUtils.generateToken(
            userId = user.id,
            email = user.email,
            secret = jwtSecret,
            issuer = jwtIssuer,
            audience = jwtAudience
        )

        return AuthResponse(token, user.id, user.email, user.name)

    }

    suspend fun login(request: LoginRequest): AuthResponse {
        if(request.email.isBlank()) {
            throw AuthException(HttpStatusCode.BadRequest, "Email cannot be empty")
        }

        if(request.password.isBlank()) {
            throw AuthException(HttpStatusCode.BadRequest, "Password cannot be empty")
        }

        val user = userRepository.findByEmail(request.email) ?: throw AuthException(HttpStatusCode.NotFound, "User not found")

        val passwordHash = userRepository.getPasswordHash(request.email)!!

        if (!BCrypt.checkpw(request.password, passwordHash)) {
            throw AuthException(HttpStatusCode.BadRequest, "Password does not match password")
        }

        val token = JwtUtils.generateToken(
            userId = user.id,
            email = user.email,
            secret = jwtSecret,
            issuer = jwtIssuer,
            audience = jwtAudience
        )

        return AuthResponse(token, user.id, user.email, user.name)
    }
}
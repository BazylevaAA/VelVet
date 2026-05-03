package com.example.app.feature.auth.data

import com.example.app.core.network.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val userId: Int,
    val email: String,
    val name: String
)

class AuthApi(private val client: HttpClient) {

    suspend fun login(email: String, password: String): AuthResponse{
        return client.post("$BASE_URL:8081/api/v1/auth/login"){
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }.body()
    }

    suspend fun register(email: String, password: String, name: String): AuthResponse {
        return client.post("$BASE_URL:8081/api/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(email, password, name))
        }.body()
    }
}
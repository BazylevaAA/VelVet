package com.example.app.feature.auth.data

import com.example.app.core.storage.TokenStorage

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage
) {
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = authApi.login(email, password)
            tokenStorage.saveAuthData(
                token = response.token,
                userId = response.userId,
                email = response.email,
                name = response.name
            )
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, name: String): Result<Unit> {
        return try {
            val response = authApi.register(email, password, name)
            tokenStorage.saveAuthData(
                token  = response.token,
                userId = response.userId,
                email  = response.email,
                name   = response.name
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        tokenStorage.clear()
    }
}
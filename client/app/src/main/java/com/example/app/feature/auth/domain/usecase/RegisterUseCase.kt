package com.example.app.feature.auth.domain.usecase

import com.example.app.feature.auth.data.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(
        email: String,
        password: String,
        name: String
    ): Result<Unit> {
        if (email.isBlank())  {
            return Result.failure(Exception("Email cannot be empty"))
        }
        if (!email.contains("@")){
            return Result.failure(Exception("Invalid email"))
        }
        if (password.length < 6){
            return Result.failure(Exception("Password min 6 characters"))
        }
        if (name.isBlank()) {
            return Result.failure(Exception("Name cannot be empty"))
        }

        return repository.register(email, password, name)
    }
}
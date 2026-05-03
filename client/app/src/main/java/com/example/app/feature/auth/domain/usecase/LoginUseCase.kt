package com.example.app.feature.auth.domain.usecase

import com.example.app.feature.auth.data.AuthRepository
import java.lang.Exception

class LoginUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        if(email.isBlank()){
            return Result.failure(Exception("Email cannot be empty !"))
        }
        if(password.length < 6){
            return Result.failure(Exception("Password too short !"))
        }
        if(!email.contains("@")){
            return Result.failure(Exception("Invalid email"))
        }

        return repository.login(email, password)
    }
}
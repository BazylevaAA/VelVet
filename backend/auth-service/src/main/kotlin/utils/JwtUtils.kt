package com.example.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.model.User
import java.util.Date

object JwtUtils {

    fun generateToken(
        userId: Int,
        email: String,
        secret: String,
        issuer: String,
        audience: String
    ): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + 24 * 3600 * 1000))
            .sign(Algorithm.HMAC256(secret))
    }
}
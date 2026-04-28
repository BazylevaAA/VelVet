package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureSecurity() {
    val secret   = environment.config.property("jwt.secret").getString()
    val issuer   = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val realm    = environment.config.property("jwt.realm").getString()

    val verifier = JWT.require(Algorithm.HMAC256(secret))
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    authentication {
        jwt("auth-jwt") {
            this.realm = realm
            this.verifier(verifier)
            validate { credential ->
                if (credential.payload.getClaim("userId").asInt() != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}

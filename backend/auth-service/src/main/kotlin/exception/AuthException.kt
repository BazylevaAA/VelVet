package com.example.exception

import io.ktor.http.HttpStatusCode

class AuthException (
    val statusCode: HttpStatusCode,
    override val message: String
) : Exception(message)
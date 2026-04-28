package com.example.exception

import io.ktor.http.HttpStatusCode

class MusicException(
    val statusCode: HttpStatusCode,
    override val message: String,
): Exception(message)
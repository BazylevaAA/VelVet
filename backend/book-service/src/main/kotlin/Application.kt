package com.example

import com.example.database.DatabaseFactory
import io.ktor.server.application.Application
import storage.MinioStorage


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init(this)
    MinioStorage.init(this)
    configureSerialization()
    configureSecurity()
    configureStatusPages()
    configureRoutong()
}
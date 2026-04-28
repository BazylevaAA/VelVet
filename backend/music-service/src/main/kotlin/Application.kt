package com.example

import com.example.database.DatabaseFactory
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.plugins.configureStatusPages
import com.example.storage.MinioStorage
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init(this)
    MinioStorage.init(this)
    configureSerialization()
    configureSecurity()
    configureStatusPages()
    configureRouting()
}

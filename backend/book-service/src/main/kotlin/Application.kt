package com.example

import com.example.database.DatabaseFactory
import io.ktor.server.application.Application
import plugins.configureSerialization
import plugins.configureSecurity
import plugins.configureStatusPages
import plugins.configureRoutong
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
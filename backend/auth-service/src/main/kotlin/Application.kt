package com.example

import com.example.database.DatabaseFactory
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.plugins.configureStatusPages
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init(this)
    configureSerialization()
    configureSecurity()
    configureStatusPages()
    configureRouting()
}

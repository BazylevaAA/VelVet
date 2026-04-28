import database.DatabaseFactory
import io.ktor.server.application.Application
import plugins.configureRouting
import plugins.configureSecurity
import plugins.configureSerialization
import plugins.configureStatusPage
import storage.MinioStorage

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init(this)
    MinioStorage.init(this)
    configureSerialization()
    configureSecurity()
    configureStatusPage()
    configureRouting()
}
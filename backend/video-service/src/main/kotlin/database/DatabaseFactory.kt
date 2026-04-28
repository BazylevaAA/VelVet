package database

import io.ktor.server.application.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.VideosTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init(application: Application) {
        val url = application.environment.config.property("database.url").getString()
        val user = application.environment.config.property("database.user").getString()
        val password = application.environment.config.property("database.password").getString()
        val driver = application.environment.config.property("database.driver").getString()

        Database.connect(url, driver, user, password)

        transaction {
            SchemaUtils.create(VideosTable)
        }
    }
}

suspend fun <T> dbQuery(block:() -> T): T = withContext(Dispatchers.IO) {
    transaction { block() }
}
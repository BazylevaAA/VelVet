package com.example.database

import com.example.model.UsersTable
import io.ktor.server.application.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init(application: Application) {
        val url = application.environment.config.property("database.url").getString()
        val user = application.environment.config.property("database.user").getString()
        val password = application.environment.config.property("database.password").getString()
        val driver = application.environment.config.property("database.driver").getString()

        Database.connect(
            url = url,
            driver = driver,
            user = user,
            password = password
        )

        transaction {
            SchemaUtils.create(UsersTable)
        }
    }

    suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO){
        transaction { block() }
    }
}
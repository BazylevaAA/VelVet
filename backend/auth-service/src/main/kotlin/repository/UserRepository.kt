package com.example.repository

import com.example.database.DatabaseFactory.dbQuery
import com.example.model.User
import com.example.model.UsersTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserRepository {

    suspend fun findByEmail(email: String): User? = dbQuery{
        UsersTable
            .selectAll().where { UsersTable.email eq email }
            .singleOrNull()
            ?.let { toUser(it) }
    }

    suspend fun existsByEmail(email: String): Boolean = dbQuery {
        UsersTable
            .selectAll().where { UsersTable.email eq email }
            .count() > 0
    }

    suspend fun create(
        email: String,
        passwordHash: String,
        name: String
    ): User = dbQuery {
        val id = UsersTable.insertAndGetId {
            it[UsersTable.email] = email
            it[UsersTable.password] = passwordHash
            it[UsersTable.name] = name
        }
        User(id.value, email,name)
    }

    suspend fun getPasswordHash(email: String): String? = dbQuery {
        UsersTable
            .selectAll().where { UsersTable.email eq email }
            .singleOrNull()
            ?.get(UsersTable.password)
    }

    private fun toUser(row: ResultRow): User {

        return User(
            id = row[UsersTable.id].value,
            email = row[UsersTable.email],
            name = row[UsersTable.name]
        )
    }
}
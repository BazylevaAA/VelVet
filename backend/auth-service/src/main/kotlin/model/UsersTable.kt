package com.example.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object UsersTable : IntIdTable("users") {

    val email = varchar("email", 255).uniqueIndex()

    val password = varchar("password_hash", 255)

    val name = varchar("name", 100)

    val createdAt = datetime("created_at").default(LocalDateTime.now())
}
package com.example.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object BookTable : IntIdTable() {
    val title = varchar("title", 255)
    val description = varchar("description", 1000)
    val author = varchar("author", 255)
    val year = integer("year")
    val fileUrl = varchar("fileUrl", 255)
    val coverUrl = varchar("coverUrl", 255)
    val userId = integer("userId")
    val createdAt = datetime("created_at").default(LocalDateTime.now())
}
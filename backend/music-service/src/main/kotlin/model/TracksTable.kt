package com.example.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object TracksTable: IntIdTable("tracks") {
    val title = varchar("title", 255)
    val artist = varchar("artist", 255)
    val album = varchar("album", 255).nullable()
    val duration = integer("duration")
    val fileUrl = varchar("fileUrl", 500)
    val coverUrl = varchar("coverUrl", 500).nullable()
    val userId = integer("userId")
    val createdAt = datetime("created_at").default(LocalDateTime.now())
}
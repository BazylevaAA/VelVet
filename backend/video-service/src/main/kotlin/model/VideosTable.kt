package model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object VideosTable: IntIdTable("videos") {
    val title       = varchar("title", 255)
    val description = varchar("description", 1000).nullable()
    val director    = varchar("director", 255).nullable()
    val year        = integer("year")
    val duration    = integer("duration")
    val genre       = varchar("genre", 100).nullable()
    val fileUrl     = varchar("file_url", 500)
    val coverUrl    = varchar("cover_url", 500).nullable()
    val userId      = integer("user_id")
    val createdAt   = datetime("created_at").default(LocalDateTime.now())
}
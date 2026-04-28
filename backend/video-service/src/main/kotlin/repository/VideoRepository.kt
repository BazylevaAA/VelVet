package repository

import database.dbQuery
import model.Video
import model.VideosTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll

class VideoRepository {

    suspend fun findAllByUserId(userId: Int): List<Video> = dbQuery {
        VideosTable
            .selectAll().where { VideosTable.userId eq userId }
            .map { toVideo(it) }
    }

    suspend fun findById(id: Int): Video? = dbQuery {
        VideosTable
            .selectAll().where { VideosTable.id eq id }
            .singleOrNull()
            ?.let { toVideo(it) }
    }

    suspend fun create(
        title: String,
        description: String?,
        director: String?,
        year: Int,
        duration: Int,
        genre: String?,
        fileUrl: String,
        coverUrl: String?,
        userId: Int
    ): Video = dbQuery {
        val id = VideosTable.insertAndGetId {
            it[VideosTable.title]       = title
            it[VideosTable.description] = description
            it[VideosTable.director]    = director
            it[VideosTable.year]        = year
            it[VideosTable.duration]    = duration
            it[VideosTable.genre]       = genre
            it[VideosTable.fileUrl]     = fileUrl
            it[VideosTable.coverUrl]    = coverUrl
            it[VideosTable.userId]      = userId
        }
        Video(id.value, title, description, director, year, duration, genre, fileUrl, coverUrl, userId)
    }

    suspend fun delete(id: Int): Boolean = dbQuery {
        VideosTable.deleteWhere { VideosTable.id eq id } > 0
    }

    suspend fun isOwner(videoId: Int, userId: Int): Boolean = dbQuery {
        VideosTable
            .selectAll().where { (VideosTable.id eq videoId) and (VideosTable.userId eq userId) }
            .count() > 0
    }


    private fun toVideo(row: ResultRow) = Video(
        id = row[VideosTable.id].value,
        title = row[VideosTable.title],
        description = row[VideosTable.description],
        director = row[VideosTable.director],
        year = row[VideosTable.year],
        duration = row[VideosTable.duration],
        genre = row[VideosTable.genre],
        fileUrl = row[VideosTable.fileUrl],
        coverUrl = row[VideosTable.coverUrl],
        userId = row[VideosTable.userId]
    )
}
package com.example.repository

import com.example.database.dbQuery
import com.example.model.Track
import com.example.model.TracksTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll

class TrackRepository {

    suspend fun finadAllByUserId(userId: Int): List<Track> = dbQuery{
        TracksTable
            .selectAll().where { TracksTable.userId eq userId }
            .map { toTrack(it) }
    }

    suspend fun findById(id: Int): Track? = dbQuery{
        TracksTable
            .selectAll().where { TracksTable.id eq id }
            .singleOrNull()
            ?.let { toTrack(it) }
    }

    suspend fun create(
        title: String,
        artist: String,
        album: String?,
        duration: Int,
        fileUrl: String,
        coverUrl: String?,
        userId: Int
    ): Track = dbQuery {
        val id = TracksTable.insertAndGetId {
            it[TracksTable.title]    = title
            it[TracksTable.artist]   = artist
            it[TracksTable.album]    = album
            it[TracksTable.duration] = duration
            it[TracksTable.fileUrl]  = fileUrl
            it[TracksTable.coverUrl] = coverUrl
            it[TracksTable.userId]   = userId
        }
        Track(id.value, title, artist, album, duration, fileUrl, coverUrl, userId)
    }

    suspend fun delete(id: Int): Boolean = dbQuery {
        TracksTable.deleteWhere { TracksTable.id eq id } > 0
    }

    suspend fun isOwner(trackId: Int, userId: Int): Boolean = dbQuery {
        TracksTable
            .selectAll().where { (TracksTable.id eq trackId) and (TracksTable.userId eq userId) }
            .count() > 0
    }

    private fun toTrack(row: ResultRow) = Track(
        id       = row[TracksTable.id].value,
        title    = row[TracksTable.title],
        artist   = row[TracksTable.artist],
        album    = row[TracksTable.album],
        duration = row[TracksTable.duration],
        fileUrl  = row[TracksTable.fileUrl],
        coverUrl = row[TracksTable.coverUrl],
        userId   = row[TracksTable.userId]
    )
}
package com.example.repository

import com.example.database.dbQuery
import com.example.model.Book
import com.example.model.BookTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll

class BookRepository{

    suspend fun findAllByUserId(userId: Int): List<Book> = dbQuery {
        BookTable
            .selectAll().where(BookTable.userId eq userId)
            .map {toBook(it)}
    }

    suspend fun findById(id: Int): Book? = dbQuery {
        BookTable
            .selectAll().where{ BookTable.id eq id }
            .singleOrNull()
            ?.let { toBook(it) }
    }

    suspend fun create(
        title: String,
        description: String,
        author: String,
        year: Int,
        fileUrl: String,
        coverUrl: String,
        uesrId: Int
    ): Book = dbQuery{
        val id = BookTable.insertAndGetId {
            it[BookTable.title] = title
            it[BookTable.description] = description
            it[BookTable.author] = author
            it[BookTable.year] = year
            it[BookTable.fileUrl] = fileUrl
            it[BookTable.coverUrl] = coverUrl
            it[BookTable.userId] = userId
        }
        Book(id.value, title, description, author, year, fileUrl, coverUrl, uesrId)
    }

    suspend fun delete(id: Int) = dbQuery {
        BookTable.deleteWhere { BookTable.id eq id }
    }

    suspend fun isOwner(bookId: Int, userId: Int): Boolean = dbQuery {
        BookTable
            .selectAll().where{ (BookTable.id eq bookId) and (BookTable.userId eq userId)}
            .count() > 0
    }


    private fun toBook(row: ResultRow): Book = Book(
        id = row[BookTable.id].value,
        title = row[BookTable.title],
        description = row[BookTable.description],
        author = row[BookTable.author],
        year = row[BookTable.year],
        fileUrl = row[BookTable.fileUrl],
        coverUrl = row[BookTable.coverUrl],
        userId = row[BookTable.userId],
    )
}
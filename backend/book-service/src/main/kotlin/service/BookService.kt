package com.example.service

import com.example.dto.request.CreateBookRequest
import com.example.dto.response.BookResponse
import com.example.exception.BookException
import com.example.model.Book
import com.example.repository.BookRepository
import io.ktor.http.*
import storage.MinioStorage
import java.io.InputStream
import java.util.UUID

class BookService(
    private val bookRepository: BookRepository
) {

    suspend fun uploadBook(
        request: CreateBookRequest,
        fileStream: InputStream,
        fileSize: Long,
        fileContentType: String,
        coverStream: InputStream?,
        coverSize: Long?,
        coverContentType: String?,
        userId: Int
    ): BookResponse {

        if (request.title.isBlank()) {
            throw BookException(HttpStatusCode.BadRequest, "Title cannot be empty")
        }
        if (request.author.isBlank()) {
            throw BookException(HttpStatusCode.BadRequest, "Author cannot be empty")
        }
        if (request.year < 1000 || request.year > 2100) {
            throw BookException(HttpStatusCode.BadRequest, "Invalid year")
        }

        val fileKey = "books/${UUID.randomUUID()}.${getExtension(fileContentType)}"

        val fileUrl = MinioStorage.uploadFile(
            key         = fileKey,
            inputStream = fileStream,
            contentType = fileContentType,
            size        = fileSize
        )

        val coverUrl = if (coverStream != null && coverSize != null && coverContentType != null) {
            val coverKey = "covers/${UUID.randomUUID()}.${getExtension(coverContentType)}"
            MinioStorage.uploadFile(
                key = coverKey,
                inputStream = coverStream,
                contentType = coverContentType,
                size = coverSize
            )
        } else null

        val book = bookRepository.create(
            title = request.title,
            description = request.description ?: "",
            author = request.author,
            year = request.year,
            fileUrl = fileUrl,
            coverUrl = coverUrl ?: "",
            userId = userId
        )

        return toResponse(book)
    }

    suspend fun getAllBooks(userId: Int): List<BookResponse> {
        return bookRepository.findAllByUserId(userId).map { toResponse(it) }
    }

    suspend fun getBookById(id: Int, userId: Int): BookResponse {
        val book = bookRepository.findById(id)
            ?: throw BookException(HttpStatusCode.NotFound, "Book not found")

        if (book.userId != userId) {
            throw BookException(HttpStatusCode.Forbidden, "Access denied")
        }

        return toResponse(book)
    }

    suspend fun deleteBook(id: Int, userId: Int) {
        val book = bookRepository.findById(id)
            ?: throw BookException(HttpStatusCode.NotFound, "Book not found")

        if (book.userId != userId) {
            throw BookException(HttpStatusCode.Forbidden, "Access denied")
        }

        val fileKey = book.fileUrl.substringAfter("velvet-book/")
        MinioStorage.deleteFile(fileKey)

        if (!book.coverUrl.isNullOrBlank()) {
            val coverKey = book.coverUrl.substringAfter("velvet-book/")
            MinioStorage.deleteFile(coverKey)
        }

        bookRepository.delete(id)
    }

    suspend fun streamBook(id: Int): InputStream {
        val book = bookRepository.findById(id)
            ?: throw BookException(HttpStatusCode.NotFound, "Book not found")
        val key = book.fileUrl.substringAfter("velvet-book/")
        return MinioStorage.getFile(key)
    }

    private fun getExtension(contentType: String): String {
        return when (contentType) {
            "application/pdf" -> "pdf"
            "application/epub+zip" -> "epub"
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "bin"
        }
    }

    private fun toResponse(book: Book) = BookResponse(
        id = book.id,
        title = book.title,
        description = book.description,
        author = book.author,
        year = book.year,
        fileUrl = book.fileUrl,
        coverUrl = book.coverUrl,
        userId = book.userId
    )
}

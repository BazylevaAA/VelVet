package com.example.app.feature.book.data

import com.example.app.feature.book.domain.model.BookModel
import com.example.app.feature.music.data.TrackDto
import com.example.app.feature.music.domain.model.TrackModel

class BookRepository(private val bookApi: BookApi) {

    suspend fun getAllBooks(): Result<List<BookModel>>{
        return try {
            val response = bookApi.getAllBooks().map { it.toModel() }
            Result.success(response)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getBookById(id: Int): Result<BookModel> {
        return try {
            val response = bookApi.getBookById(id).toModel()
            Result.success(response)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun deleteTrack(id: Int): Result<Unit> {
        return try {
            bookApi.deleteBook(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun streamBook(id: Int): Result<ByteArray> {
        return try {
            Result.success(bookApi.streamBook(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private fun BookDto.toModel() = BookModel(
        id = id,
        title = title,
        description = description,
        author = author,
        year = year,
        fileUrl  = fileUrl,
        coverUrl = coverUrl,
        userId   = userId
    )
}
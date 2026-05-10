package com.example.app.feature.book.domain.usecase

import com.example.app.feature.book.data.BookRepository
import com.example.app.feature.book.domain.model.BookModel

class GetBooksUseCase(private val repository: BookRepository) {

    suspend operator fun invoke(): Result<List<BookModel>>{
        return repository.getAllBooks()
    }
}
package com.example.app.feature.book.domain.usecase

import com.example.app.feature.book.data.BookRepository

class DeleteBookUseCase(private val repository: BookRepository) {

    suspend operator fun invoke(id: Int): Result<Unit>{
        return repository.deleteTrack(id)
    }
}
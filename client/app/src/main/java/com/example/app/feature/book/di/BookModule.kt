package com.example.app.feature.book.di

import com.example.app.feature.book.data.BookApi
import com.example.app.feature.book.data.BookRepository
import com.example.app.feature.book.domain.usecase.DeleteBookUseCase
import com.example.app.feature.book.domain.usecase.GetBooksUseCase
import com.example.app.feature.book.presentation.BookViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val bookModule = module{
    single { BookApi(get(), get()) }
    single { BookRepository(get()) }

    factory { GetBooksUseCase(get()) }
    factory { DeleteBookUseCase(get()) }

    viewModel { BookViewModel(get(), get(), get()) }
}
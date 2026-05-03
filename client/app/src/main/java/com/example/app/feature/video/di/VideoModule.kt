package com.example.app.feature.video.di

import com.example.app.feature.movie.presentation.VideoViewModel
import com.example.app.feature.video.data.VideoApi
import com.example.app.feature.video.data.VideoRepository
import com.example.app.feature.video.domain.usecase.DeleteVideoUseCase
import com.example.app.feature.video.domain.usecase.GetVideoUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val videoModule = module {

    single { VideoApi(get(), get()) }
    single { VideoRepository(get()) }

    factory { GetVideoUseCase(get()) }
    factory { DeleteVideoUseCase(get()) }

    viewModel { VideoViewModel(get(), get()) }
}
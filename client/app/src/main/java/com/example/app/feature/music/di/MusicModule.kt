package com.example.app.feature.music.di

import org.koin.androidx.viewmodel.dsl.viewModel
import com.example.app.feature.music.data.MusicApi
import com.example.app.feature.music.data.MusicRepository
import com.example.app.feature.music.domain.usecase.DeleteTrackUseCase
import com.example.app.feature.music.domain.usecase.GetTracksUseCase
import com.example.app.feature.music.presentation.MusicViewModel
import org.koin.dsl.module

val musicModule = module {

    single { MusicApi(get(), get()) }
    single { MusicRepository(get()) }

    factory { GetTracksUseCase(get()) }
    factory { DeleteTrackUseCase(get()) }

    viewModel { MusicViewModel(get(), get()) }
}
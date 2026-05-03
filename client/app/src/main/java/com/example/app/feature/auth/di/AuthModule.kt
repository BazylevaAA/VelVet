package com.example.app.feature.auth.di

import com.example.app.core.network.createHttpClient
import com.example.app.core.storage.TokenStorage
import com.example.app.feature.auth.data.AuthApi
import com.example.app.feature.auth.data.AuthRepository
import com.example.app.feature.auth.domain.usecase.LoginUseCase
import com.example.app.feature.auth.domain.usecase.RegisterUseCase
import com.example.app.feature.auth.presentation.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val authModule = module{

    single { createHttpClient() }
    single { TokenStorage(get()) }
    single { AuthApi(get()) }
    single { AuthRepository(get(), get()) }

    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }

    viewModel { AuthViewModel(get(), get()) }
}
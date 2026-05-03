package com.example.app

import android.app.Application
import com.example.app.feature.auth.di.authModule
import com.example.app.feature.home.di.homeModule
import com.example.app.feature.music.di.musicModule
import com.example.app.feature.video.di.videoModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                authModule,
                musicModule,
                videoModule,
                homeModule
            )
        }
    }
}
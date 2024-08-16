package com.example.musicapp.app

import android.app.Application
import com.example.musicapp.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MusicApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.BUILD_TYPE == "debug") {
            Timber.plant(Timber.DebugTree())
        }
    }
}
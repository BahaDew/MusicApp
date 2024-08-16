package com.example.musicapp.di

import android.content.Context
import com.example.musicapp.domain.MusicRepository
import com.example.musicapp.domain.impl.MusicRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @[Provides Singleton]
    fun bindMusicRepository(@ApplicationContext context: Context) : MusicRepository {
        return MusicRepositoryImpl(context.contentResolver)
    }
}
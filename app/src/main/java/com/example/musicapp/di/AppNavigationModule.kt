package com.example.musicapp.di

import com.example.musicapp.navigation.AppNavigationDispatcher
import com.example.musicapp.navigation.AppNavigationHandler
import com.example.musicapp.navigation.AppNavigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppNavigationModule {

    @[Binds Singleton]
    fun bindAppNavigator(impl : AppNavigationDispatcher) : AppNavigator

    @[Binds Singleton]
    fun bindAppNavigationHandler(impl : AppNavigationDispatcher) : AppNavigationHandler
}
package com.example.musicapp.navigation

import androidx.navigation.NavDirections

interface AppNavigator {

    suspend fun navigateTo(directions: NavDirections)

    suspend fun navigateUp()
}
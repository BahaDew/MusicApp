package com.example.musicapp.navigation

import kotlinx.coroutines.flow.Flow


interface AppNavigationHandler {
    val buffer : Flow<AppNavigation>
}
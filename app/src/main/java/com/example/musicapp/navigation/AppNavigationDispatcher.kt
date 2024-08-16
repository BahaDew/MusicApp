package com.example.musicapp.navigation

import androidx.navigation.NavDirections
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNavigationDispatcher @Inject constructor() : AppNavigator, AppNavigationHandler {
    override val buffer = MutableSharedFlow<AppNavigation>()

    private suspend fun navigate(appNavigation: AppNavigation) {
        buffer.emit(appNavigation)
    }

    override suspend fun navigateTo(directions: NavDirections) = navigate {
        this.navigate(directions)
    }

    override suspend fun navigateUp() = navigate {
        this.popBackStack()
    }
}
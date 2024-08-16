package com.example.musicapp.ui.viewmodel.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.navigation.AppNavigator
import com.example.musicapp.ui.screen.SplashScreenDirections
import com.example.musicapp.ui.viewmodel.SplashViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModelImpl @Inject constructor(
//    private val appNavigator: AppNavigator
) : ViewModel(), SplashViewModel {
    override fun openSplash() {
//        viewModelScope.launch {
//            appNavigator.navigateTo(SplashScreenDirections.actionSplashScreenToMainScreen())
//        }
    }

}
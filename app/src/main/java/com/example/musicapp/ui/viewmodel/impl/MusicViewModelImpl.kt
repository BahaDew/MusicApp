package com.example.musicapp.ui.viewmodel.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.MusicRepository
import com.example.musicapp.navigation.AppNavigator
import com.example.musicapp.ui.viewmodel.MusicViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModelImpl @Inject constructor(
//    private val appNavigator: AppNavigator,
    private val musicRepository: MusicRepository
) : ViewModel(), MusicViewModel {

    override fun onClickBack() {
//        viewModelScope.launch {
//            appNavigator.navigateUp()
//        }
    }

}
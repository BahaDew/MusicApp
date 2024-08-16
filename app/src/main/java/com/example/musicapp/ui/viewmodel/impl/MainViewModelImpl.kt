package com.example.musicapp.ui.viewmodel.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.MusicData
import com.example.musicapp.domain.MusicRepository
import com.example.musicapp.navigation.AppNavigator
import com.example.musicapp.ui.screen.MainScreenDirections
import com.example.musicapp.ui.viewmodel.MainViewModel
import com.example.musicapp.utils.musicList
import com.example.musicapp.utils.musicLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModelImpl @Inject constructor(
//    private val appNavigator: AppNavigator,
    private val musicRepository: MusicRepository
) : ViewModel(), MainViewModel {

    override val allMusicList = MutableStateFlow<List<MusicData>>(emptyList())
    override val refreshState = MutableStateFlow(false)

    override fun requestAllMusic() {
        musicLogger("Request keldi", "MUS")
        refreshState.value = true
        musicRepository
            .getAllMusicList()
            .onEach {
                refreshState.value = false
                it.onSuccess { list ->
                    musicList = list
                    allMusicList.tryEmit(list)
                }.onFailure { thr ->
                    musicLogger("${thr.message}")
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onClickMusic(musicData: MusicData) {
//        viewModelScope.launch {
//            appNavigator.navigateTo(MainScreenDirections.actionMainScreenToMusicScreen(musicData))
//        }
    }
}
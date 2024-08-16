package com.example.musicapp.ui.viewmodel

import com.example.musicapp.data.MusicData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MainViewModel {

    val allMusicList : StateFlow<List<MusicData>>
    val refreshState : StateFlow<Boolean>

    fun requestAllMusic()

    fun onClickMusic(musicData: MusicData)
}
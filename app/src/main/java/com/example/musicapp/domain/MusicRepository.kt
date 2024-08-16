package com.example.musicapp.domain

import com.example.musicapp.data.MusicData
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    fun getAllMusicList() : Flow<Result<List<MusicData>>>
}
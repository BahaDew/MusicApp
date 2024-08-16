package com.example.musicapp.utils

import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import com.example.musicapp.data.MusicData

var selectMusicPos: Int = -1
var musicList: List<MusicData> = emptyList()
var mediaPlayer: MediaPlayer? = null
var changeProgressState: Long = 0L

var currentTime: Long = 0L
var fullTime: Long = 0L

val currentTimeLiveData = MutableLiveData<Long>()

val playMusicLiveData = MutableLiveData<MusicData>()
val isPlayingLiveData = MutableLiveData<Boolean>()


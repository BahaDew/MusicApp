package com.example.musicapp.utils

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private val projection = arrayOf(
    MediaStore.Audio.Media._ID,
    MediaStore.Audio.Media.ARTIST,
    MediaStore.Audio.Media.TITLE,
    MediaStore.Audio.Media.DATA,
    MediaStore.Audio.Media.DURATION
)

fun Context.getMusicsCursor(): Flow<Cursor> = flow {
    val cursor: Cursor = contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        MediaStore.Audio.Media.IS_MUSIC + "!=0",
        null,
        null
    ) ?: return@flow
    emit(cursor)
}
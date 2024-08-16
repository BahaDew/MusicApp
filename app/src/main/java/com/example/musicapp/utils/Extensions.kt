package com.example.musicapp.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.data.MusicData
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

fun musicLogger(message: String, tag: String = "BAHA") {
    Timber.tag(tag).d(message)
}

fun Cursor.toMusicData(position: Int): MusicData {
    this.moveToPosition(position)
    val imagePath = getString(getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
    val musicPath = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
    var bitmap : Bitmap? = null;
    val file = File(musicPath)
    if (file.exists()) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(imagePath)
        val coverBytes = retriever.embeddedPicture
        if (coverBytes != null) {
            bitmap = BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.size)
        }
    }
    return MusicData(
        id = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
        title = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
        musicPath = musicPath,
        duration = getInt(getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
        size = getInt(getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)),
        artist = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
        image = bitmap
    )
}
package com.example.musicapp.domain.impl

import android.content.ContentResolver
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
import com.example.musicapp.data.MusicData
import com.example.musicapp.domain.MusicRepository
import com.example.musicapp.utils.toMusicData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : MusicRepository {

    private val sortOrder = "${MediaStore.Audio.Media.DISC_NUMBER} ASC"
    private val selection = "is_music != 0 AND title != ''"
    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Images.Media.DATA
    )

    override fun getAllMusicList(): Flow<Result<List<MusicData>>> =
        callbackFlow<Result<List<MusicData>>> {
            val cursor = contentResolver.query(
                EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )
            val retriever = MediaMetadataRetriever()
            if (cursor == null) {
                trySend(Result.failure(Throwable("Nul Pointer")))
            } else {
                val list = ArrayList<MusicData>()
                for (i in 0..<cursor.count) {
                    cursor.apply {
                        moveToPosition(i)
                        val imagePath = getString(getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                        val musicPath = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                        var bitmap: Bitmap? = null;
                        val file = File(musicPath)
                        if (file.exists()) {
                            retriever.setDataSource(imagePath)
                            val coverBytes = retriever.embeddedPicture
                            if (coverBytes != null) {
                                bitmap =
                                    BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.size)
                            }
                        }
                        list.add(
                            MusicData(
                                id = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
                                title = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
                                musicPath = musicPath,
                                duration = getInt(getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                                size = getInt(getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)),
                                artist = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
                                image = bitmap
                            )
                        )
                    }
                }
                cursor.close()
                trySend(Result.success(list))
            }
            retriever.release()
            awaitClose()
        }.flowOn(Dispatchers.IO)
}
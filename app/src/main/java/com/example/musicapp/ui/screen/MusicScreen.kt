package com.example.musicapp.ui.screen

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.musicapp.R
import com.example.musicapp.data.ActionEnum
import com.example.musicapp.data.MusicData
import com.example.musicapp.databinding.ScreenMusicBinding
import com.example.musicapp.ui.service.MyService
import com.example.musicapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class MusicScreen : Fragment(R.layout.screen_music) {
    private val binding by viewBinding(ScreenMusicBinding::bind)

    private lateinit var onBack: OnBackPressedCallback
    var onBackEvent: (() -> Unit)? = null

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goBackScreen()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBack)
        initView()
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun initView() = binding.apply {
        var statusBarHeight = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        binding.statusBar.layoutParams.apply {
            height = statusBarHeight
        }
        next.setOnClickListener { startMyService(ActionEnum.NEXT) }
        prev.setOnClickListener { startMyService(ActionEnum.PREV) }
        playPause.setOnClickListener {
            if (mediaPlayer!!.isPlaying)
                startMyService(ActionEnum.PAUSE)
            else {
                startMyService(ActionEnum.RESUME)
            }
        }
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                musicLogger("progress -> $progress fromUser -> $fromUser", "seek")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                musicLogger("onstart progress -> ${seekBar?.progress}", "seek")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                musicLogger(" onstop progress -> ${seekBar?.progress}", "seek")
                changeProgressState = binding.seekBar.progress.toLong()
                startMyService(ActionEnum.PROGRESS)
            }
        })
        playMusicLiveData.observe(viewLifecycleOwner, playMusicObserver)
        isPlayingLiveData.observe(viewLifecycleOwner, isPlayingObserver)
        currentTimeLiveData.observe(viewLifecycleOwner, currentTimeObserver)
        btnBack.setOnClickListener { goBackScreen() }
    }


    private fun startMyService(actionEnum: ActionEnum) {
        val intent = Intent(requireContext(), MyService::class.java)
        intent.putExtra("COMMAND", actionEnum)
        if (Build.VERSION.SDK_INT >= 26) {
            requireActivity().startForegroundService(intent)
        } else requireActivity().startService(intent)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private val playMusicObserver = Observer<MusicData> {
        binding.seekBar.max = it.duration
        binding.musicName.text = it.title
        binding.end.text = getDateFormat(it.duration.toLong())
        binding.author.text = it.artist
        val renderEffect = RenderEffect.createBlurEffect(500f, 500f, Shader.TileMode.CLAMP)
        if (it.image != null) {
            lifecycleScope.launch(Dispatchers.Main) {
                val isLight = isImageLight(it.image)
                val window = requireActivity().window
                val wicc = WindowInsetsControllerCompat(window, window.decorView)
                wicc.isAppearanceLightStatusBars = isLight
                musicLogger("Is Light = $isLight", "LGT")
                binding.apply {
                    btnBack.isSelected = isLight
                    musicName.isSelected = isLight
                    author.isSelected = isLight
                    seekBar.isSelected = isLight
                    start.isSelected = isLight
                    end.isSelected = isLight
                    next.isSelected = isLight
                    playPause.isSelected = isLight
                    prev.isSelected = isLight
                }
            }
            binding.musicImg.setImageBitmap(it.image)
            binding.bgImage.setImageBitmap(it.image)
            binding.musicImg.setPadding(0, 0, 0, 0)
        } else {
            binding.musicImg.setImageResource(R.drawable.ic_music_note_svgrepo_com)
            binding.musicImg.setPadding(170, 200, 200, 200)
            binding.bgImage.setImageResource(R.drawable.bg1)
            val window = requireActivity().window
            val wicc = WindowInsetsControllerCompat(window, window.decorView)
            wicc.isAppearanceLightStatusBars = false
            binding.apply {
                btnBack.isSelected = false
                musicName.isSelected = false
                author.isSelected = false
                seekBar.isSelected = false
                start.isSelected = false
                end.isSelected = false
                next.isSelected = false
                playPause.isSelected = false
                prev.isSelected = false
            }
        }
        binding.bgImage.setRenderEffect(renderEffect)
    }

    private val isPlayingObserver = Observer<Boolean> {
        if (it) binding.playPause.setImageResource(R.drawable.pause_circle_svgrepo_com)
        else binding.playPause.setImageResource(R.drawable.play_circle_svgrepo_com)
    }

    private val currentTimeObserver = Observer<Long> {
        binding.seekBar.progress = it.toInt()
        binding.start.text = getDateFormat(it)
    }

    private fun getDateFormat(mill: Long): String {
        return if (mill < 3_600_000) {
            val formatter = SimpleDateFormat("mm:ss", Locale.ROOT)
            formatter.timeZone = TimeZone.getTimeZone("GMT")
            formatter.format(mill)
        } else {
            val formatter = SimpleDateFormat("HH:mm:ss", Locale.ROOT)
            formatter.timeZone = TimeZone.getTimeZone("GMT")
            formatter.format(mill)
        }
    }

    private fun goBackScreen() {
        parentFragmentManager
            .popBackStack()
        onBackEvent?.invoke()
        onBack.remove()
    }

    private fun isImageLight(bitmap: Bitmap): Boolean {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
        val pixels = IntArray(scaledBitmap.width * scaledBitmap.height)
        scaledBitmap.getPixels(pixels, 0, scaledBitmap.width, 0, 0, scaledBitmap.width, scaledBitmap.height)

        var totalBrightness = 0
        for (pixel in pixels) {
            val brightness = Color.red(pixel) * 0.299 + Color.green(pixel) * 0.587 + Color.blue(pixel) * 0.114
            totalBrightness += brightness.toInt()
        }

        val avgBrightness = totalBrightness / pixels.size
        return avgBrightness > 128
    }
}
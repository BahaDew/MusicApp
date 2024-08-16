package com.example.musicapp.ui.screen

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.musicapp.R
import com.example.musicapp.data.ActionEnum
import com.example.musicapp.data.MusicData
import com.example.musicapp.databinding.ScreenMainBinding
import com.example.musicapp.ui.adapter.MusicListAdapter
import com.example.musicapp.ui.service.MyService
import com.example.musicapp.ui.viewmodel.MainViewModel
import com.example.musicapp.ui.viewmodel.impl.MainViewModelImpl
import com.example.musicapp.utils.isPlayingLiveData
import com.example.musicapp.utils.mediaPlayer
import com.example.musicapp.utils.musicLogger
import com.example.musicapp.utils.playMusicLiveData
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class MainScreen : Fragment(R.layout.screen_main) {
    private val binding by viewBinding(ScreenMainBinding::bind)
    private val viewModel: MainViewModel by viewModels<MainViewModelImpl>()
    private val musicListAdapter = MusicListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initFlow()
    }

    private fun initView() = binding.apply {
        var statusBarHeight = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        binding.temp.setOnClickListener { }
        binding.temp.visibility = View.GONE
        binding.statusBar.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight)
        rvList.adapter = musicListAdapter
        rvList.layoutManager = LinearLayoutManager(requireContext())
        musicListAdapter.setOnClickMusic {
            val file = File(it.musicPath)
            if (file.exists()) {
                startMyService(ActionEnum.PLAY)
                musicLogger("Music bosildi", "BBT")
                goMusicScreen(it)
            } else {
                viewModel.requestAllMusic()
            }
        }
        srl.setOnRefreshListener {
            musicLogger("Refresh bo'ldi", "MUS")
            lifecycleScope.launch {
                delay(400)
                srl.isRefreshing = false
            }
        }
        requireActivity().window.statusBarColor = Color.parseColor("#242F3D")
    }

    private fun initFlow() = binding.apply {
        playMusicLiveData.observe(viewLifecycleOwner, playMusicObserver)
        isPlayingLiveData.observe(viewLifecycleOwner, isPlayingObserver)
        viewModel.allMusicList
            .onEach {
                musicLogger("List keldi", "MUS")
                musicListAdapter.submitList(it)
            }
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)

        viewModel
            .refreshState
            .onEach {
                srl.isRefreshing = it
            }
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)
    }

    private fun startMyService(actionEnum: ActionEnum) {
        val intent = Intent(requireContext(), MyService::class.java)
        intent.putExtra("COMMAND", actionEnum)
        if (Build.VERSION.SDK_INT >= 26) {
            requireActivity().startForegroundService(intent)
        } else requireActivity().startService(intent)
    }

    private val playMusicObserver = Observer<MusicData> {
        binding.tagPlayer.isVisible = true
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val renderEffect = RenderEffect.createBlurEffect(10f, 10f, Shader.TileMode.CLAMP)
//        } else {
////            blurImage(binding.tagBg, 300f)
//        }

        binding.tagPlayer.setOnClickListener { _ ->
            val file = File(it.musicPath)
            if (file.exists()) {
                musicLogger("Tag player bosildi", "BBT")
                goMusicScreen(musicData = it)
            } else {
                mediaPlayer?.stop()
                viewModel.requestAllMusic()
                startMyService(ActionEnum.NEXT)
            }
        }
        binding.btnManage.setOnClickListener {
            startMyService(if (mediaPlayer!!.isPlaying) ActionEnum.PAUSE else ActionEnum.RESUME)
        }
        musicListAdapter.playingChange()
        binding.tagName.text = it.title
        binding.tagAuthor.text = it.artist
        if (it.image != null) {
            binding.tagImg.setImageBitmap(it.image)
            binding.tagImg.setPadding(0, 0, 0, 0)
        } else {
            binding.tagImg.setImageResource(R.drawable.ic_music_note_svgrepo_com)
            binding.tagImg.setPadding(20, 25, 25, 25)
        }
    }

    private val isPlayingObserver = Observer<Boolean> {
        musicListAdapter.setManageState(it)
        if (it) binding.btnManage.setImageResource(R.drawable.pause_svgrepo_com)
        else binding.btnManage.setImageResource(R.drawable.play_svgrepo_com)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(
                permissions = listOf(
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                allGrantedOk = {
                    viewModel.requestAllMusic()
                },
                allGrantedNo = {
                    showAlertDialog(
                        onClickOk = {
                            requestPermission(
                                permissions = listOf(
                                    Manifest.permission.READ_MEDIA_AUDIO,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ),
                                allGrantedOk = { viewModel.requestAllMusic() },
                                allGrantedNo = { requireActivity().finish() }
                            )
                        },
                        onClickCancel = {
                            requireActivity().finish()
                        }
                    )
                }
            )
        } else {
            requestPermission(
                permissions = listOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                allGrantedOk = {
                    viewModel.requestAllMusic()
                },
                allGrantedNo = {
                    showAlertDialog(
                        onClickOk = {
                            requestPermission(
                                permissions = listOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                ),
                                allGrantedOk = { viewModel.requestAllMusic() },
                                allGrantedNo = { requireActivity().finish() }
                            )
                        },
                        onClickCancel = {
                            requireActivity().finish()
                        }
                    )
                }
            )
        }
    }

    private fun showAlertDialog(onClickOk: () -> Unit, onClickCancel: () -> Unit) {
        AlertDialog.Builder(requireActivity())
            .setTitle("Muhim")
            .setMessage("Iltimos, ruxsatlarni berishni tasdiqlang !")
            .setPositiveButton("Ruxsat berish") { dialog, _ ->
                onClickOk.invoke()
                dialog.dismiss()
            }
            .setNegativeButton("Bekor qilish") { dialog, _ ->
                onClickCancel.invoke()
                dialog.dismiss()
            }
            .show()
    }

    private fun requestPermission(
        permissions: List<String>,
        allGrantedOk: () -> Unit,
        allGrantedNo: () -> Unit
    ) {
        PermissionX
            .init(this)
            .permissions(permissions)
            .request { allGranted, _, _ ->
                if (allGranted) {
                    allGrantedOk.invoke()
                } else {
                    allGrantedNo.invoke()
                }
            }
    }

    private fun goMusicScreen(musicData: MusicData) {
        val musicScreen = MusicScreen()
        binding.temp.visibility = View.VISIBLE
        musicScreen.onBackEvent = {
            binding.temp.visibility = View.GONE
            val window = requireActivity().window
            val wicc = WindowInsetsControllerCompat(window, window.decorView)
            wicc.isAppearanceLightStatusBars = false
        }
        parentFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_up,
                R.anim.slide_down,
                R.anim.slide_up,
                R.anim.slide_down
            )
            .add(R.id.container, musicScreen, musicScreen.toString())
            .addToBackStack(musicScreen.toString())
            .commit()
    }

//    private fun blurImage(imageView: ImageView, radius: Float) {
//        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
//        val width = Math.round(bitmap.width * 0.4f)
//        val height = Math.round(bitmap.height * 0.4f)
//        val inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
//        val outputBitmap = Bitmap.createBitmap(inputBitmap)
//
//        val renderScript = RenderScript.create(imageView.context)
//        val input = Allocation.createFromBitmap(renderScript, inputBitmap)
//        val output = Allocation.createFromBitmap(renderScript, outputBitmap)
//        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
//        script.setRadius(radius)
//        script.setInput(input)
//        script.forEach(output)
//        output.copyTo(outputBitmap)
//        imageView.setImageBitmap(outputBitmap)
//    }
}
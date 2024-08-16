package com.example.musicapp.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.musicapp.R
import com.example.musicapp.databinding.ScreenSplashBinding
import com.example.musicapp.ui.viewmodel.SplashViewModel
import com.example.musicapp.ui.viewmodel.impl.SplashViewModelImpl
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreen : Fragment(R.layout.screen_splash) {
    private val binding by viewBinding(ScreenSplashBinding::bind)
    private val viewModel: SplashViewModel by viewModels<SplashViewModelImpl>()
    private var isResume = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        lifecycleScope.launch {
            delay(1000)
            checkPermission()
        }
        requireActivity().window.statusBarColor = Color.parseColor("#FFFFFF")
    }

    override fun onResume() {
        super.onResume()
        if (isResume) {
            checkPermission()
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(
                permissions = listOf(
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                allGrantedOk = {
                    goMainScreen()
                },
                allGrantedNo = {
                    showAlertDialog(
                        onClickOk = {
                            requestPermission(
                                permissions = listOf(
                                    Manifest.permission.READ_MEDIA_AUDIO,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ),
                                allGrantedOk = { goMainScreen() },
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
                    goMainScreen()
                },
                allGrantedNo = {
                    showAlertDialog(
                        onClickOk = {
                            requestPermission(
                                permissions = listOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                ),
                                allGrantedOk = { goMainScreen() },
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

    override fun onStop() {
        super.onStop()
        isResume = true
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

    private fun goMainScreen() {
        viewModel.openSplash()
        val mainScreen = MainScreen()
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.container, mainScreen, mainScreen.toString())
            .commit()
    }
}
package com.example.musicapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.ui.screen.SplashScreen
import com.example.musicapp.utils.musicLogger
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

//    @Inject
//    lateinit var appNavigationHandler: AppNavigationHandler
    private val binding by viewBinding(ActivityMainBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
//        val navController = navHostFragment.navController
//
//        appNavigationHandler.buffer
//            .onEach { it.invoke(navController) }
//            .launchIn(lifecycleScope)
        var statusBarHeight = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        val splashScreen = SplashScreen()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, splashScreen, splashScreen.toString())
            .commit()
    }
}
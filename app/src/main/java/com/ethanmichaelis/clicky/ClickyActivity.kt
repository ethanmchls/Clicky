package com.ethanmichaelis.clicky

import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.ethanmichaelis.clicky.nav.ClickyNavHost
import com.ethanmichaelis.clicky.ui.theme.ClickyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ClickyActivity : ComponentActivity() {

    private val viewModel: ClickyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        viewModel.handleDeepLinks(intent = intent) {
            Timber.d("$it")
        }
        lifecycleScope.launch {
            viewModel.init()
        }
        setContentView(R.layout.clicky_activity)
        val composeView = ComposeView(this).apply {
            setContent {
                val initialized by viewModel.initialized.collectAsState()
                Timber.d("Drawing first frames")
                if (initialized) ClickyTheme {
                    Timber.d("Post initialization")
                    val navController = rememberNavController()
                    ClickyNavHost(navController = navController, viewModel = viewModel)
                }
            }
        }
        val content = findViewById<FrameLayout>(R.id.clicky_container)
        content.apply {
            addView(composeView)
            viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        return if (viewModel.initialized.value) {
                            content.viewTreeObserver.removeOnPreDrawListener(this)
                            true
                        } else {
                            false
                        }
                    }
                }
            )
        }
    }
}
package com.ioslauncher.app

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ioslauncher.app.ui.LauncherLock
import com.ioslauncher.app.ui.LauncherRoot
import com.ioslauncher.app.ui.theme.IosLauncherTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        applyImmersive()
        setContent {
            IosLauncherTheme {
                LauncherRoot()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        applyImmersive()
        // Tells the launcher to show its lock screen again after the user
        // comes back from another app.
        LauncherLock.bump()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) applyImmersive()
    }

    /**
     * Hides the system status and navigation bars so the launcher's own bars
     * are the only ones on screen - this is what makes the phone's three-button
     * navigation disappear.
     *
     * Android deliberately gives no API to disable the notification shade or
     * quick-settings panel itself, so a swipe from the very top edge can still
     * pull the system one down. No launcher can prevent that; the launcher's own
     * Control Centre is what the in-app gestures open.
     */
    private fun applyImmersive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        }
    }
}

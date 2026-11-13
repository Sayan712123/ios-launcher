package com.ioslauncher.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ioslauncher.app.data.AppLauncher
import com.ioslauncher.app.data.RecentEntry
import com.ioslauncher.app.ui.apps.BuiltinAppHost
import com.ioslauncher.app.ui.components.IosWallpaper
import com.ioslauncher.app.ui.components.LauncherNavBar

sealed interface LauncherScreen {
    data object Home : LauncherScreen
    data object Library : LauncherScreen
    data object Search : LauncherScreen
    data object Recents : LauncherScreen
    data class Builtin(val id: String) : LauncherScreen
    data class Folder(val name: String) : LauncherScreen
}

@Composable
fun LauncherRoot() {
    val vm: LauncherViewModel = viewModel()
    val context = LocalContext.current

    var screen by remember { mutableStateOf<LauncherScreen>(LauncherScreen.Home) }
    var showControlCenter by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    var locked by remember { mutableStateOf(vm.lockScreenEnabled) }

    // MainActivity bumps this on every resume, so returning from another app
    // brings the lock screen back.
    LaunchedEffect(LauncherLock.tick) {
        if (vm.lockScreenEnabled) locked = true
    }

    BackHandler(
        enabled = screen != LauncherScreen.Home || showControlCenter || showNotifications
    ) {
        when {
            showControlCenter -> showControlCenter = false
            showNotifications -> showNotifications = false
            screen != LauncherScreen.Home -> screen = LauncherScreen.Home
            else -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ---- Lock screen ------------------------------------------------
        if (locked) {
            LockScreen(
                wallpaperStyle = vm.wallpaperStyle,
                customWallpaper = vm.customWallpaper,
                darkWallpaper = vm.darkWallpaper,
                onUnlock = { locked = false },
                onOpenNotifications = { showNotifications = true }
            )
            BackHandler { locked = false }
            return@Box
        }

        // ---- Wallpaper behind every launcher surface --------------------
        IosWallpaper(
            style = vm.wallpaperStyle,
            dark = vm.darkWallpaper,
            customUri = vm.customWallpaper
        )

        // ---- Normal launcher surface -----------------------------------
        when (val s = screen) {
            LauncherScreen.Home -> HomeScreen(
                vm = vm,
                onOpenBuiltin = { id ->
                    val b = builtinById(id)
                    if (b != null) AppLauncher.openBuiltin(context, b.id, b.name)
                    screen = LauncherScreen.Builtin(id)
                },
                onOpenLibrary = { screen = LauncherScreen.Library },
                onOpenSearch = { screen = LauncherScreen.Search },
                onOpenFolder = { screen = LauncherScreen.Folder(it) }
            )

            LauncherScreen.Library -> AppLibraryScreen(
                vm = vm,
                onBack = { screen = LauncherScreen.Home },
                onOpenBuiltin = { id ->
                    val b = builtinById(id)
                    if (b != null) AppLauncher.openBuiltin(context, b.id, b.name)
                    screen = LauncherScreen.Builtin(id)
                }
            )

            LauncherScreen.Search -> SearchScreen(
                vm = vm,
                onBack = { screen = LauncherScreen.Home },
                onOpenBuiltin = { id ->
                    val b = builtinById(id)
                    if (b != null) AppLauncher.openBuiltin(context, b.id, b.name)
                    screen = LauncherScreen.Builtin(id)
                }
            )

            LauncherScreen.Recents -> RecentsScreen(
                onOpen = { entry: RecentEntry ->
                    val isBuiltin = builtinById(entry.key) != null
                    AppLauncher.openRecent(context, entry)
                    if (isBuiltin) screen = LauncherScreen.Builtin(entry.key)
                },
                onHome = { screen = LauncherScreen.Home },
                onDismiss = { screen = LauncherScreen.Home }
            )

            is LauncherScreen.Folder -> FolderScreen(
                vm = vm,
                folderName = s.name,
                onBack = { screen = LauncherScreen.Home },
                onOpenBuiltin = { id ->
                    val b = builtinById(id)
                    if (b != null) AppLauncher.openBuiltin(context, b.id, b.name)
                    screen = LauncherScreen.Builtin(id)
                }
            )

            is LauncherScreen.Builtin -> BuiltinAppHost(
                id = s.id,
                vm = vm,
                onBack = { screen = LauncherScreen.Home }
            )
        }

        // ---- Gesture zones on the home screen ---------------------------
        // Top-left pull  -> notifications, top-right pull -> Control Centre.
        // Bottom edge swipe up -> recents.
        if (screen == LauncherScreen.Home) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.12f)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, drag ->
                            if (drag > 6f) showNotifications = true
                        }
                    }
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.12f)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, drag ->
                            if (drag > 6f) showControlCenter = true
                        }
                    }
            )

            // Bottom gesture strip: swipe up = recents.
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(28.dp)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onVerticalDrag = { _, drag ->
                                if (drag < -25f) screen = LauncherScreen.Recents
                            }
                        )
                    }
            )
        }

        // ---- Overlays ---------------------------------------------------
        AnimatedVisibility(
            visible = showControlCenter,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it }
        ) {
            ControlCenterScreen(
                fakeBlur = vm.fakeBlur,
                onDismiss = { showControlCenter = false }
            )
        }

        AnimatedVisibility(
            visible = showNotifications,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it }
        ) {
            NotificationCenterScreen(onDismiss = { showNotifications = false })
        }

        // ---- Our navigation bar (replaces the system 3-button bar) -------
        if (screen == LauncherScreen.Home && !showControlCenter && !showNotifications) {
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                LauncherNavBar(
                    style = vm.navStyle,
                    visible = vm.customNavBar,
                    onBack = { screen = LauncherScreen.Home },
                    onHome = { screen = LauncherScreen.Home },
                    onRecents = { screen = LauncherScreen.Recents }
                )
            }
        }
    }
}

/**
 * Bumped by MainActivity.onResume so the root can re-show the lock screen when
 * the user returns from another app. A plain object keeps this out of the
 * ViewModel and needs no lifecycle observer.
 */
object LauncherLock {
    var tick by mutableStateOf(0)
        private set

    fun bump() {
        tick++
    }
}

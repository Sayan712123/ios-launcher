package com.ioslauncher.app.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Tiny preference store for the launcher layout: dock apps, folders and hidden
 * apps. Everything is a plain string so there is no JSON parser on the hot path
 * and no database to open on startup - which keeps a low-end phone fast.
 */
class LauncherPrefs(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("launcher_layout", Context.MODE_PRIVATE)

    var dockKeys: List<String>
        get() = prefs.getString(KEY_DOCK, null)
            ?.split('\n')
            ?.filter { it.isNotBlank() }
            ?: emptyList()
        set(value) = prefs.edit().putString(KEY_DOCK, value.joinToString("\n")).apply()

    var hiddenKeys: Set<String>
        get() = prefs.getStringSet(KEY_HIDDEN, emptySet())?.toSet() ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_HIDDEN, value).apply()

    /** Folders are stored as `name|key1,key2` lines. */
    var folders: List<Folder>
        get() = prefs.getString(KEY_FOLDERS, null)
            ?.split('\n')
            ?.mapNotNull { line ->
                val parts = line.split('|', limit = 2)
                if (parts.size != 2) return@mapNotNull null
                val name = parts[0]
                val keys = parts[1].split(',').filter { it.isNotBlank() }
                if (name.isBlank() || keys.isEmpty()) null else Folder(name, keys)
            }
            ?: emptyList()
        set(value) = prefs.edit().putString(
            KEY_FOLDERS,
            value.joinToString("\n") { "${it.name}|${it.appKeys.joinToString(",")}" }
        ).apply()

    var darkWallpaper: Boolean
        get() = prefs.getBoolean(KEY_DARK_WALLPAPER, true)
        set(value) = prefs.edit().putBoolean(KEY_DARK_WALLPAPER, value).apply()

    var wallpaperStyle: Int
        get() = prefs.getInt(KEY_WALLPAPER_STYLE, 0)
        set(value) = prefs.edit().putInt(KEY_WALLPAPER_STYLE, value).apply()

    /** Custom wallpaper picked from the gallery, stored as a persistable URI. */
    var customWallpaper: String?
        get() = prefs.getString(KEY_CUSTOM_WALLPAPER, null)
        set(value) = prefs.edit().putString(KEY_CUSTOM_WALLPAPER, value).apply()

    /** Selected third-party icon pack package, or null for the system icons. */
    var iconPack: String?
        get() = prefs.getString(KEY_ICON_PACK, null)
        set(value) = prefs.edit().putString(KEY_ICON_PACK, value).apply()

    /** iOS-style lock screen shown in front of the home screen. */
    var lockScreenEnabled: Boolean
        get() = prefs.getBoolean(KEY_LOCK_SCREEN, true)
        set(value) = prefs.edit().putBoolean(KEY_LOCK_SCREEN, value).apply()

    /**
     * Cheap "frosted glass" look inside Control Centre. A real RenderEffect
     * blur re-renders the whole screen every frame on the GPU; on a POCO M4
     * that costs frame rate, so this draws gradient scrims instead.
     */
    var fakeBlur: Boolean
        get() = prefs.getBoolean(KEY_FAKE_BLUR, true)
        set(value) = prefs.edit().putBoolean(KEY_FAKE_BLUR, value).apply()

    /**
     * Try to keep the system notification shade / quick settings out of the
     * way. Android has no public API to disable them outright, so this drives
     * the immersive-mode workaround (see README).
     */
    var blockSystemControlCenter: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_SYSTEM_CC, true)
        set(value) = prefs.edit().putBoolean(KEY_BLOCK_SYSTEM_CC, value).apply()

    /** Navigation style: 0 = on-screen buttons, 1 = gesture pill. */
    var navStyle: Int
        get() = prefs.getInt(KEY_NAV_STYLE, 1)
        set(value) = prefs.edit().putInt(KEY_NAV_STYLE, value).apply()

    /** Draw our own navigation bar and hide the system one. */
    var customNavBar: Boolean
        get() = prefs.getBoolean(KEY_CUSTOM_NAV_BAR, true)
        set(value) = prefs.edit().putBoolean(KEY_CUSTOM_NAV_BAR, value).apply()

    private companion object {
        const val KEY_DOCK = "dock"
        const val KEY_HIDDEN = "hidden"
        const val KEY_FOLDERS = "folders"
        const val KEY_DARK_WALLPAPER = "dark_wallpaper"
        const val KEY_WALLPAPER_STYLE = "wallpaper_style"
        const val KEY_CUSTOM_WALLPAPER = "custom_wallpaper"
        const val KEY_ICON_PACK = "icon_pack"
        const val KEY_LOCK_SCREEN = "lock_screen"
        const val KEY_FAKE_BLUR = "fake_blur"
        const val KEY_BLOCK_SYSTEM_CC = "block_system_cc"
        const val KEY_NAV_STYLE = "nav_style"
        const val KEY_CUSTOM_NAV_BAR = "custom_nav_bar"
    }
}

data class Folder(val name: String, val appKeys: List<String>)

package com.ioslauncher.app.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ioslauncher.app.data.AppInfo
import com.ioslauncher.app.data.AppRepository
import com.ioslauncher.app.data.Folder
import com.ioslauncher.app.data.IconPack
import com.ioslauncher.app.data.LauncherPrefs
import com.ioslauncher.app.data.RecentTasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface HomeItem {
    data class App(val app: AppInfo) : HomeItem
    data class FolderItem(val folder: Folder) : HomeItem
    data class Builtin(val id: String) : HomeItem
}

class LauncherViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = LauncherPrefs(app)

    var apps by mutableStateOf<List<AppInfo>>(emptyList())
        private set
    var dockKeys by mutableStateOf<List<String>>(emptyList())
        private set
    var hiddenKeys by mutableStateOf<Set<String>>(emptySet())
        private set
    var folders by mutableStateOf<List<Folder>>(emptyList())
        private set
    var wallpaperStyle by mutableStateOf(0)
        private set
    var darkWallpaper by mutableStateOf(true)
        private set
    var customWallpaper by mutableStateOf<String?>(null)
        private set
    var iconPack by mutableStateOf<String?>(null)
        private set
    var fakeBlur by mutableStateOf(true)
        private set
    var lockScreenEnabled by mutableStateOf(true)
        private set
    var blockSystemControlCenter by mutableStateOf(true)
        private set
    var navStyle by mutableStateOf(1)
        private set
    var customNavBar by mutableStateOf(true)
        private set
    var loading by mutableStateOf(true)
        private set

    init {
        reload()
    }

    fun reload() {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                AppRepository.apps(getApplication(), force = true)
            }
            apps = list
            hiddenKeys = prefs.hiddenKeys
            folders = prefs.folders
            wallpaperStyle = prefs.wallpaperStyle
            darkWallpaper = prefs.darkWallpaper
            customWallpaper = prefs.customWallpaper
            iconPack = prefs.iconPack
            fakeBlur = prefs.fakeBlur
            lockScreenEnabled = prefs.lockScreenEnabled
            blockSystemControlCenter = prefs.blockSystemControlCenter
            navStyle = prefs.navStyle
            customNavBar = prefs.customNavBar
            if (iconPack != null) {
                withContext(Dispatchers.IO) { IconPack.setPack(getApplication(), iconPack) }
            }
            withContext(Dispatchers.IO) { RecentTasks.load(getApplication()) }
            val saved = prefs.dockKeys
            dockKeys = if (saved.isEmpty()) defaultDock(list) else saved
            loading = false
        }
    }

    fun appByKey(key: String): AppInfo? = apps.firstOrNull { it.key == key }

    val dockApps: List<AppInfo>
        get() = dockKeys.mapNotNull { key -> apps.firstOrNull { it.key == key } }

    /** Apps shown on the home pages: built-ins first, then installed apps. */
    val homeItems: List<HomeItem>
        get() {
            val inFolders = folders.flatMap { it.appKeys }.toSet()
            val folderItems = folders.map { HomeItem.FolderItem(it) }
            val builtinItems = builtinApps.map { HomeItem.Builtin(it.id) }
            val appItems = apps
                .filter { it.key !in hiddenKeys && it.key !in inFolders }
                .map { HomeItem.App(it) }
            return builtinItems + folderItems + appItems
        }

    fun setDock(keys: List<String>) {
        dockKeys = keys.take(4)
        prefs.dockKeys = dockKeys
    }

    fun addToDock(app: AppInfo) {
        if (dockKeys.contains(app.key)) return
        setDock(dockKeys + app.key)
    }

    fun hide(app: AppInfo) {
        hiddenKeys = hiddenKeys + app.key
        prefs.hiddenKeys = hiddenKeys
        if (dockKeys.contains(app.key)) setDock(dockKeys - app.key)
    }

    fun unhideAll() {
        hiddenKeys = emptySet()
        prefs.hiddenKeys = emptySet()
    }

    fun createFolderWith(app: AppInfo): Folder {
        val name = "Folder ${folders.size + 1}"
        val folder = Folder(name, listOf(app.key))
        folders = folders + folder
        prefs.folders = folders
        return folder
    }

    fun addToFolder(folderName: String, app: AppInfo) {
        folders = folders.map { f ->
            if (f.name == folderName && !f.appKeys.contains(app.key)) {
                f.copy(appKeys = f.appKeys + app.key)
            } else f
        }
        prefs.folders = folders
    }

    fun removeFromFolder(folderName: String, appKey: String) {
        folders = folders.mapNotNull { f ->
            if (f.name != folderName) f
            else {
                val remaining = f.appKeys - appKey
                if (remaining.isEmpty()) null else f.copy(appKeys = remaining)
            }
        }
        prefs.folders = folders
    }

    fun renameFolder(oldName: String, newName: String) {
        if (newName.isBlank()) return
        folders = folders.map { if (it.name == oldName) it.copy(name = newName) else it }
        prefs.folders = folders
    }

    fun deleteFolder(name: String) {
        folders = folders.filterNot { it.name == name }
        prefs.folders = folders
    }

    fun setWallpaper(style: Int) {
        wallpaperStyle = style
        customWallpaper = null
        prefs.wallpaperStyle = style
        prefs.customWallpaper = null
    }

    fun setCustomWallpaper(uri: String?) {
        customWallpaper = uri
        prefs.customWallpaper = uri
    }

    fun setDarkWallpaper(dark: Boolean) {
        darkWallpaper = dark
        prefs.darkWallpaper = dark
    }

    fun setIconPack(packageName: String?) {
        iconPack = packageName
        prefs.iconPack = packageName
        viewModelScope.launch(Dispatchers.IO) {
            IconPack.setPack(getApplication(), packageName)
        }
    }

    fun setFakeBlur(on: Boolean) {
        fakeBlur = on
        prefs.fakeBlur = on
    }

    fun setLockScreen(on: Boolean) {
        lockScreenEnabled = on
        prefs.lockScreenEnabled = on
    }

    fun setBlockSystemControlCenter(on: Boolean) {
        blockSystemControlCenter = on
        prefs.blockSystemControlCenter = on
    }

    fun setNavStyle(style: Int) {
        navStyle = style
        prefs.navStyle = style
    }

    fun setCustomNavBar(on: Boolean) {
        customNavBar = on
        prefs.customNavBar = on
    }

    private fun defaultDock(list: List<AppInfo>): List<String> {
        val hints = listOf(
            listOf("dialer", "phone", "contacts"),
            listOf("messaging", "mms", "sms", "messages"),
            listOf("chrome", "browser", "firefox", "opera"),
            listOf("camera", "gcam")
        )
        val picked = ArrayList<String>(4)
        for (hint in hints) {
            val match = list.firstOrNull { app ->
                val p = app.packageName.lowercase()
                hint.any { p.contains(it) } && app.key !in picked
            }
            if (match != null) picked.add(match.key)
        }
        if (picked.size < 4) {
            for (app in list) {
                if (picked.size >= 4) break
                if (!picked.contains(app.key)) picked.add(app.key)
            }
        }
        return picked
    }
}

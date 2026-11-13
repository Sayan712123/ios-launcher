package com.ioslauncher.app.data

import android.content.ComponentName
import android.content.pm.ApplicationInfo

/**
 * A single launchable app on the device. Deliberately holds no Drawable:
 * icons are rasterised on demand and kept in a small LruCache so a low-end
 * device never keeps hundreds of full-size drawables in memory.
 */
data class AppInfo(
    val label: String,
    val packageName: String,
    val activityName: String,
    val component: ComponentName,
    val isSystem: Boolean
) {
    val key: String get() = "$packageName/$activityName"

    companion object {
        fun isSystemApp(flags: Int): Boolean =
            (flags and ApplicationInfo.FLAG_SYSTEM) != 0
    }
}

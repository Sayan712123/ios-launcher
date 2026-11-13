package com.ioslauncher.app.data

import android.app.ActivityManager
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * Recent-apps list for the launcher's own recents panel.
 *
 * Why our own list instead of the system one: `ActivityManager.getRunningTasks`
 * is deprecated and returns only the caller's own tasks on modern Android, and
 * `UsageStatsManager` needs a special-access grant. So we record what the
 * launcher itself opens, which is enough for a launcher: every app the user
 * launches from here lands in this list, and the panel can close those apps.
 *
 * The list is capped at 12 entries so memory and startup cost stay tiny.
 */
object RecentTasks {

    private const val MAX = 12
    private const val PREFS = "launcher_recents"
    private const val REC = "\u001E"
    private const val FLD = "\u001F"

    val items: SnapshotStateList<RecentEntry> = mutableStateListOf()
    private var loaded = false

    fun load(context: Context) {
        if (loaded) return
        loaded = true
        val raw = context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString("items", null) ?: return
        items.clear()
        raw.split(REC).forEach { block ->
            if (block.isBlank()) return@forEach
            val p = block.split(FLD)
            if (p.size >= 3) {
                items.add(
                    RecentEntry(
                        key = p[0],
                        label = p[1],
                        openedAt = p[2].toLongOrNull() ?: 0L
                    )
                )
            }
        }
    }

    private fun save(context: Context) {
        val raw = items.joinToString(REC) { "${it.key}$FLD${it.label}$FLD${it.openedAt}" }
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString("items", raw).apply()
    }

    fun record(context: Context, key: String, label: String) {
        if (key.isBlank()) return
        items.removeAll { it.key == key }
        items.add(0, RecentEntry(key, label, System.currentTimeMillis()))
        while (items.size > MAX) items.removeAt(items.lastIndex)
        save(context)
    }

    /** Drop one entry without touching the app process. */
    fun remove(context: Context, key: String) {
        items.removeAll { it.key == key }
        save(context)
    }

    fun clear(context: Context) {
        items.clear()
        save(context)
    }

    /**
     * Ask the platform to kill the app's background processes.
     *
     * This only ever succeeds for apps the launcher started itself and that
     * are running in the background; Android deliberately blocks one app from
     * killing another's foreground process, so a swipe here is a request, not
     * a guarantee. Returns true when the call was accepted.
     */
    fun killBackground(context: Context, packageName: String): Boolean = try {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        am.killBackgroundProcesses(packageName)
        remove(context, packageName)
        true
    } catch (t: Throwable) {
        false
    }
}

data class RecentEntry(val key: String, val label: String, val openedAt: Long)

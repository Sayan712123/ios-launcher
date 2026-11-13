package com.ioslauncher.app.data

import android.content.ComponentName
import android.content.Context
import com.ioslauncher.app.ui.builtinById

/**
 * Single place that opens things, so the recents list always stays in sync
 * with what the launcher actually launched.
 *
 * Keys are either a built-in app id (`calculator`, `phone`, ...) or an installed
 * app's `packageName/activityName`.
 */
object AppLauncher {

    fun openBuiltin(context: Context, id: String, label: String) {
        RecentTasks.record(context, id, label)
    }

    fun openInstalled(context: Context, app: AppInfo): Boolean {
        val ok = AppRepository.launch(context, app)
        if (ok) RecentTasks.record(context, app.key, app.label)
        return ok
    }

    /**
     * Re-open an entry from the recents panel. Built-ins resolve by id, real
     * apps by the component recorded in their key.
     */
    fun openRecent(context: Context, entry: RecentEntry): Boolean {
        val builtin = builtinById(entry.key)
        if (builtin != null) {
            RecentTasks.record(context, builtin.id, builtin.name)
            return true
        }
        val parts = entry.key.split('/')
        if (parts.size != 2) return false
        val app = AppInfo(
            label = entry.label,
            packageName = parts[0],
            activityName = parts[1],
            component = ComponentName(parts[0], parts[1]),
            isSystem = false
        )
        val ok = AppRepository.launch(context, app)
        if (ok) RecentTasks.record(context, entry.key, entry.label)
        return ok
    }
}

package com.ioslauncher.app.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Reads the installed-app list once and caches it. Icons are rasterised to a
 * small bitmap (132px) and stored in a byte-bounded LruCache, which keeps the
 * launcher's memory footprint flat even with 200+ apps installed.
 */
object AppRepository {

    private const val ICON_PX = 132
    private const val ICON_CACHE_BYTES = 6 * 1024 * 1024

    private val iconCache = object : LruCache<String, ImageBitmap>(ICON_CACHE_BYTES) {
        override fun sizeOf(key: String, value: ImageBitmap): Int =
            value.width * value.height * 4
    }

    @Volatile
    private var cachedApps: List<AppInfo>? = null

    fun apps(context: Context, force: Boolean = false): List<AppInfo> {
        if (!force) cachedApps?.let { return it }
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val resolved: List<ResolveInfo> = try {
            pm.queryIntentActivities(intent, 0)
        } catch (t: Throwable) {
            emptyList()
        }

        val seen = HashSet<String>(resolved.size)
        val out = ArrayList<AppInfo>(resolved.size)
        for (ri in resolved) {
            val ai = ri.activityInfo ?: continue
            val pkg = ai.packageName ?: continue
            // One entry per package: the first launcher activity wins.
            if (!seen.add(pkg)) continue
            val label = try {
                ri.loadLabel(pm)?.toString()?.takeIf { it.isNotBlank() } ?: pkg
            } catch (t: Throwable) {
                pkg
            }
            val flags = try {
                pm.getApplicationInfo(pkg, 0).flags
            } catch (t: Throwable) {
                0
            }
            out.add(
                AppInfo(
                    label = label,
                    packageName = pkg,
                    activityName = ai.name,
                    component = ComponentName(pkg, ai.name),
                    isSystem = AppInfo.isSystemApp(flags)
                )
            )
        }
        out.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.label })
        cachedApps = out
        return out
    }

    fun peekIcon(key: String): ImageBitmap? = iconCache.get(key)

    fun icon(context: Context, app: AppInfo): ImageBitmap? {
        iconCache.get(app.key)?.let { return it }
        val pm = context.packageManager
        val drawable = try {
            pm.getActivityIcon(app.component)
        } catch (t: Throwable) {
            try {
                pm.getApplicationIcon(app.packageName)
            } catch (t2: Throwable) {
                null
            }
        } ?: return null

        return try {
            val bmp = Bitmap.createBitmap(ICON_PX, ICON_PX, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            drawable.setBounds(0, 0, ICON_PX, ICON_PX)
            drawable.draw(canvas)
            val image = bmp.asImageBitmap()
            iconCache.put(app.key, image)
            image
        } catch (t: Throwable) {
            null
        }
    }

    fun invalidate() {
        cachedApps = null
        iconCache.evictAll()
    }

    fun launch(context: Context, app: AppInfo): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            component = app.component
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        }
        return try {
            context.startActivity(intent)
            true
        } catch (t: Throwable) {
            false
        }
    }

    fun isInstalled(context: Context, packageName: String): Boolean = try {
        context.packageManager.getApplicationInfo(packageName, 0)
        true
    } catch (t: Throwable) {
        false
    }
}

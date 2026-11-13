package com.ioslauncher.app.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.LruCache
import android.util.Xml
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import org.xmlpull.v1.XmlPullParser
import java.util.zip.ZipFile

/**
 * Third-party icon pack support (ADW / Nova / Lawnchair style packs).
 *
 * A pack is an APK that ships an `appfilter.xml` mapping
 * `ComponentInfo{pkg/cls}` to a drawable name. We read that file straight out
 * of the pack's APK with a streaming pull parser, so nothing is held in memory
 * beyond the small component -> drawable-name map.
 *
 * Icons are rasterised to 132px and kept in a byte-bounded LruCache, exactly
 * like the system icons, so a pack never inflates the launcher's footprint.
 */
object IconPack {

    private const val ICON_PX = 132
    private const val CACHE_BYTES = 4 * 1024 * 1024

    private val cache = object : LruCache<String, ImageBitmap>(CACHE_BYTES) {
        override fun sizeOf(key: String, value: ImageBitmap): Int =
            value.width * value.height * 4
    }

    @Volatile
    private var componentMap: Map<String, String>? = null

    @Volatile
    private var packPackage: String? = null

    /** Every installed app that advertises itself as an icon pack. */
    fun availablePacks(context: Context): List<IconPackInfo> {
        val pm = context.packageManager
        val intent = Intent("org.adw.launcher.THEMES")
        val resolved = try {
            pm.queryIntentActivities(intent, 0)
        } catch (t: Throwable) {
            emptyList()
        }
        return resolved.mapNotNull { ri ->
            val pkg = ri.activityInfo?.packageName ?: return@mapNotNull null
            val label = try {
                ri.loadLabel(pm)?.toString()?.takeIf { it.isNotBlank() }
            } catch (t: Throwable) {
                null
            } ?: pkg
            IconPackInfo(pkg, label)
        }.distinctBy { it.packageName }
    }

    /** True when the pack actually contains an appfilter.xml we can read. */
    fun isUsable(context: Context, packageName: String): Boolean = try {
        val apk = context.packageManager.getApplicationInfo(packageName, 0).sourceDir
        ZipFile(apk).use { it.getEntry("appfilter.xml") != null }
    } catch (t: Throwable) {
        false
    }

    fun currentPack(): String? = packPackage

    fun setPack(context: Context, packageName: String?) {
        if (packageName == packPackage) return
        packPackage = packageName
        componentMap = null
        cache.evictAll()
        if (packageName == null) return
        componentMap = try {
            parseAppFilter(context, packageName)
        } catch (t: Throwable) {
            emptyMap()
        }
    }

    private fun parseAppFilter(context: Context, packageName: String): Map<String, String> {
        val out = HashMap<String, String>(512)
        val apk = context.packageManager.getApplicationInfo(packageName, 0).sourceDir
        ZipFile(apk).use { zip ->
            val entry = zip.getEntry("appfilter.xml") ?: return emptyMap()
            zip.getInputStream(entry).use { input ->
                val parser = Xml.newPullParser()
                parser.setInput(input, null)
                var event = parser.eventType
                while (event != XmlPullParser.END_DOCUMENT) {
                    if (event == XmlPullParser.START_TAG && parser.name == "item") {
                        val component = parser.getAttributeValue(null, "component")
                        val drawable = parser.getAttributeValue(null, "drawable")
                        if (component != null && drawable != null) {
                            val clean = component
                                .removePrefix("ComponentInfo{")
                                .removeSuffix("}")
                            if (clean.isNotBlank()) out[clean] = drawable
                        }
                    }
                    event = parser.next()
                }
            }
        }
        return out
    }

    /** Icon from the active pack, or null when the pack has no entry for this app. */
    fun icon(context: Context, app: AppInfo): ImageBitmap? {
        val pkg = packPackage ?: return null
        val cacheKey = "$pkg:${app.key}"
        cache.get(cacheKey)?.let { return it }

        val drawableName = componentMap?.get(app.key) ?: return null
        val res = try {
            context.packageManager.getResourcesForApplication(pkg)
        } catch (t: Throwable) {
            return null
        }
        val id = res.getIdentifier(drawableName, "drawable", pkg)
        if (id == 0) return null
        val drawable: Drawable = try {
            @Suppress("DEPRECATION")
            res.getDrawable(id, null)
        } catch (t: Throwable) {
            return null
        } ?: return null

        return try {
            val bmp = Bitmap.createBitmap(ICON_PX, ICON_PX, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            drawable.setBounds(0, 0, ICON_PX, ICON_PX)
            drawable.draw(canvas)
            val image = bmp.asImageBitmap()
            cache.put(cacheKey, image)
            image
        } catch (t: Throwable) {
            null
        }
    }

    fun invalidate() {
        componentMap = null
        cache.evictAll()
    }
}

data class IconPackInfo(val packageName: String, val label: String)

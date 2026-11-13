package com.ioslauncher.app.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.provider.Settings

/** Thin wrappers over the system intents the launcher hands off to. */
object SystemUtils {

    fun openAppInfo(context: Context, packageName: String) {
        safeStart(
            context,
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:$packageName"))
        )
    }

    fun openWifiSettings(context: Context) = safeStart(
        context,
        Intent(Settings.ACTION_WIFI_SETTINGS)
    )

    fun openBluetoothSettings(context: Context) = safeStart(
        context,
        Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
    )

    fun openAirplaneSettings(context: Context) = safeStart(
        context,
        Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
    )

    fun openDisplaySettings(context: Context) = safeStart(
        context,
        Intent(Settings.ACTION_DISPLAY_SETTINGS)
    )

    fun openSoundSettings(context: Context) = safeStart(
        context,
        Intent(Settings.ACTION_SOUND_SETTINGS)
    )

    fun openLauncherSettings(context: Context) = safeStart(
        context,
        Intent(Settings.ACTION_HOME_SETTINGS)
    )

    fun openNotificationAccessSettings(context: Context) = safeStart(
        context,
        Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
    )

    fun openCamera(context: Context) {
        safeStart(context, Intent("android.media.action.STILL_IMAGE_CAMERA"))
    }

    fun openDialer(context: Context) {
        safeStart(context, Intent(Intent.ACTION_DIAL))
    }

    fun openBrowser(context: Context) {
        safeStart(context, Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")))
    }

    fun openMessages(context: Context) {
        safeStart(
            context,
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MESSAGING)
        )
    }

    fun expandStatusBar(context: Context) {
        val intent = Intent("android.intent.action.QUICK_SETTINGS")
        safeStart(context, intent)
    }

    fun setBrightness(context: Context, value: Float) {
        // Writing the system brightness needs WRITE_SETTINGS, which is a
        // special-access grant. Without it the slider is a no-op rather than
        // a crash, and the user can still change brightness in Settings.
        runCatching {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                (value.coerceIn(0f, 1f) * 255).toInt()
            )
        }
    }

    fun hasFlashlight(context: Context): Boolean = try {
        val cm = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cm.cameraIdList.any { id ->
            cm.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    } catch (t: Throwable) {
        false
    }

    fun setFlashlight(context: Context, on: Boolean): Boolean {
        return try {
            val cm = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val id = cm.cameraIdList.firstOrNull { cid ->
                cm.getCameraCharacteristics(cid)
                    .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
            if (id == null) {
                false
            } else {
                cm.setTorchMode(id, on)
                true
            }
        } catch (t: Throwable) {
            false
        }
    }

    fun isDefaultLauncher(context: Context): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val res = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.resolveActivity(
                intent,
                android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.resolveActivity(intent, 0)
        }
        val name: ComponentName? = res?.activityInfo?.let {
            ComponentName(it.packageName, it.name)
        }
        return name?.packageName == context.packageName
    }

    private fun safeStart(context: Context, intent: Intent) {
        runCatching {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}

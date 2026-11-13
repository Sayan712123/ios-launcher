package com.ioslauncher.app.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.provider.Settings

/** Thin wrappers around the system intents the launcher needs. */
object SystemUtils {

    fun openAppInfo(context: Context, packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        runCatching { context.startActivity(intent) }
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
        val intent = Intent(android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
    }

    fun openDialer(context: Context) {
        val intent = Intent(Intent.ACTION_DIAL).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
    }

    fun openBrowser(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
    }

    fun openMessages(context: Context) {
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_APP_MESSAGING)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
    }

    fun expandStatusBar(context: Context) {
        val intent = Intent("android.intent.action.QUICK_PANEL")
        runCatching { context.sendBroadcast(intent) }
    }

    /** Screen brightness 0f..1f, or -1f to hand control back to the system. */
    fun setBrightness(context: Context, value: Float) {
        val activity = context as? Activity ?: return
        activity.runOnUiThread {
            val params = activity.window.attributes
            params.screenBrightness = value
            activity.window.attributes = params
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

    fun setFlashlight(context: Context, on: Boolean): Boolean = try {
        val cm = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val id = cm.cameraIdList.firstOrNull { cid ->
            cm.getCameraCharacteristics(cid)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        } ?: return false
        cm.setTorchMode(id, on)
        true
    } catch (t: Throwable) {
        false
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
        return res?.activityInfo?.packageName == context.packageName
    }

    private fun safeStart(context: Context, intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
    }
}

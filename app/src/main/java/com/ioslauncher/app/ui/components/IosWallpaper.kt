package com.ioslauncher.app.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Wallpaper layer.
 *
 * Gradients are the default because they cost nothing to draw - no decode, no
 * full-screen bitmap in memory, which is what makes the launcher feel instant
 * on a low-end POCO. When the user picks a photo from the gallery it is decoded
 * downsampled (long edge ~1440px, RGB_565) off the main thread and reused.
 */
@Composable
fun IosWallpaper(
    style: Int,
    dark: Boolean,
    customUri: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var bitmap by remember(customUri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(customUri) {
        bitmap = null
        if (customUri.isNullOrBlank()) return@LaunchedEffect
        bitmap = withContext(Dispatchers.IO) { decodeSampled(context, customUri) }
    }

    val palette = when (style % 4) {
        0 -> listOf(Color(0xFF0B1026), Color(0xFF1B2A6B), Color(0xFF3B1E63), Color(0xFF0A0A12))
        1 -> listOf(Color(0xFF2B1055), Color(0xFF7597DE), Color(0xFFE8A87C), Color(0xFF1A1030))
        2 -> listOf(Color(0xFF101014), Color(0xFF2C2C34), Color(0xFF3A3A44), Color(0xFF0C0C10))
        else -> listOf(Color(0xFF001B2E), Color(0xFF0A4D68), Color(0xFF088395), Color(0xFF00121F))
    }
    val colors = if (dark) palette else palette.map { it.copy(alpha = 0.92f) }

    Box(modifier = modifier.fillMaxSize()) {
        val image = bitmap
        if (image != null) {
            Image(
                bitmap = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = colors,
                            start = Offset.Zero,
                            end = Offset.Infinite
                        )
                    )
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colors[1].copy(alpha = 0.55f), Color.Transparent),
                        center = Offset(w * 0.15f, h * 0.12f),
                        radius = w * 0.85f
                    ),
                    radius = w * 0.85f,
                    center = Offset(w * 0.15f, h * 0.12f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colors[2].copy(alpha = 0.45f), Color.Transparent),
                        center = Offset(w * 0.9f, h * 0.75f),
                        radius = w * 0.9f
                    ),
                    radius = w * 0.9f,
                    center = Offset(w * 0.9f, h * 0.75f)
                )
            }
        }
    }
}

/**
 * Two-pass decode: read bounds, pick a power-of-two sample size that lands the
 * long edge near 1440px, then decode. A 4000x3000 photo takes roughly 4 MB
 * instead of 48 MB.
 */
private fun decodeSampled(context: Context, uriString: String): ImageBitmap? = try {
    val uri = Uri.parse(uriString)
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, bounds)
    }
    val longEdge = maxOf(bounds.outWidth, bounds.outHeight).coerceAtLeast(1)
    var sample = 1
    while (longEdge / sample > 1440) sample *= 2

    val opts = BitmapFactory.Options().apply {
        inSampleSize = sample
        inPreferredConfig = Bitmap.Config.RGB_565
    }
    val bmp: Bitmap? = context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, opts)
    }
    bmp?.asImageBitmap()
} catch (t: Throwable) {
    null
}

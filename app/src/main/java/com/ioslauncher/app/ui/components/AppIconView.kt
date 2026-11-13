package com.ioslauncher.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.data.AppInfo
import com.ioslauncher.app.data.AppRepository
import com.ioslauncher.app.data.IconPack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * iOS-style squircle app icon.
 *
 * Resolution order: active icon pack first, then the app's own icon. Both go
 * through byte-bounded LruCaches and are only ever read on Dispatchers.IO, so
 * scrolling the grid never blocks the UI thread and memory stays flat.
 */
@Composable
fun AppIconView(
    app: AppInfo,
    size: Dp,
    modifier: Modifier = Modifier,
    useIconPack: Boolean = true
) {
    val context = LocalContext.current

    // Cache hits are cheap and safe to read synchronously; anything that has to
    // touch the package manager is deferred to the IO dispatcher below.
    var bitmap by remember(app.key) {
        mutableStateOf(
            if (useIconPack) IconPack.icon(context, app) ?: AppRepository.peekIcon(app.key)
            else AppRepository.peekIcon(app.key)
        )
    }

    LaunchedEffect(app.key, useIconPack) {
        if (bitmap == null) {
            bitmap = withContext(Dispatchers.IO) {
                (if (useIconPack) IconPack.icon(context, app) else null)
                    ?: AppRepository.icon(context, app)
            }
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.225f))
            .background(Color(0x1AFFFFFF)),
        contentAlignment = Alignment.Center
    ) {
        val image: ImageBitmap? = bitmap
        if (image != null) {
            Image(
                bitmap = image,
                contentDescription = app.label,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                app.label.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                color = Color.White,
                fontSize = (size.value * 0.4f).sp
            )
        }
    }
}

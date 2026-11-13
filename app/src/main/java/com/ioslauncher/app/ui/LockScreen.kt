package com.ioslauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.service.LauncherNotificationListener
import com.ioslauncher.app.service.NotificationItem
import com.ioslauncher.app.ui.components.IosStatusBar
import com.ioslauncher.app.ui.components.IosWallpaper
import com.ioslauncher.app.ui.theme.IosColors
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * iOS-style lock screen: big date/time, live notification cards, and
 * swipe-up-to-unlock. It is drawn inside the launcher's own window, so it
 * needs no keyguard permission and behaves the same on any POCO/MIUI build.
 */
@Composable
fun LockScreen(
    wallpaperStyle: Int,
    customWallpaper: String?,
    darkWallpaper: Boolean,
    onUnlock: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    val items by LauncherNotificationListener.notifications.collectAsState()
    var now by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            delay(15_000L)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        IosWallpaper(
            style = wallpaperStyle,
            dark = darkWallpaper,
            customUri = customWallpaper
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.34f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { _, delta ->
                            if (delta < -18f) onUnlock()
                        }
                    )
                }
        ) {
            IosStatusBar()

            Spacer(Modifier.height(18.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
                        .format(now)
                        .uppercase(),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    SimpleDateFormat("h:mm", Locale.getDefault()).format(now),
                    color = Color.White,
                    fontSize = 92.sp,
                    fontWeight = FontWeight.Thin
                )
            }

            Spacer(Modifier.height(10.dp))

            if (items.isEmpty()) {
                Spacer(Modifier.weight(1f))
            } else {
                Text(
                    "NOTIFICATIONS",
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 22.dp, bottom = 6.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    items(items.take(5), key = { it.id }) { item ->
                        LockNotificationCard(item)
                    }
                }
                Spacer(Modifier.weight(1f))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .clickable { onOpenNotifications() }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Swipe up to open",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(width = 132.dp, height = 5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))
                        .clickable { onUnlock() }
                )
            }
        }
    }
}

@Composable
private fun LockNotificationCard(item: NotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(IosColors.Blue.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                item.title.firstOrNull()?.uppercaseChar()?.toString() ?: "A",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.size(9.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (item.text.isNotBlank()) {
                Text(
                    item.text,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Text(
            SimpleDateFormat("h:mm", Locale.getDefault()).format(Date(item.postedAt)),
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 10.sp
        )
    }
}

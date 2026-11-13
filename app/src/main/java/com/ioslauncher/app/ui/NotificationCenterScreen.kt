package com.ioslauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.service.LauncherNotificationListener
import com.ioslauncher.app.service.NotificationItem
import com.ioslauncher.app.ui.components.IosStatusBar
import com.ioslauncher.app.ui.theme.IosColors
import com.ioslauncher.app.util.SystemUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** iOS Notification Centre: big clock, then stacked notification cards. */
@Composable
fun NotificationCenterScreen(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val items by LauncherNotificationListener.notifications.collectAsState()
    val connected by LauncherNotificationListener.isConnected.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.78f))
    ) {
        IosStatusBar()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                SimpleDateFormat("h:mm", Locale.getDefault()).format(Date()),
                color = Color.White,
                fontSize = 76.sp,
                fontWeight = FontWeight.Thin
            )
        }

        if (!connected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.14f))
                    .clickable { SystemUtils.openNotificationAccessSettings(context) }
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        "Enable notification access",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Tap to let the launcher show your notifications here.",
                        color = IosColors.SecondaryLabel,
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No Notifications",
                    color = IosColors.SecondaryLabel,
                    fontSize = 15.sp
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    NotificationCard(item)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 26.dp)
                .size(width = 120.dp, height = 5.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.5f))
                .clickable { onDismiss() }
        )
    }
}

@Composable
private fun NotificationCard(item: NotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(IosColors.Blue.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                item.title.firstOrNull()?.uppercaseChar()?.toString() ?: "A",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    item.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    SimpleDateFormat("h:mm", Locale.getDefault()).format(Date(item.postedAt)),
                    color = IosColors.SecondaryLabel,
                    fontSize = 11.sp
                )
            }
            if (item.text.isNotBlank()) {
                Text(
                    item.text,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

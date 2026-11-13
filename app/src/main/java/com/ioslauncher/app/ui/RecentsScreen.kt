package com.ioslauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.data.AppRepository
import com.ioslauncher.app.data.RecentEntry
import com.ioslauncher.app.data.RecentTasks
import com.ioslauncher.app.ui.components.AppIconView
import com.ioslauncher.app.ui.components.GlyphIcon
import com.ioslauncher.app.ui.components.IosStatusBar
import com.ioslauncher.app.ui.theme.IosColors

/**
 * The launcher's own recents panel.
 *
 * Android has no public API for a third-party app to enumerate or close every
 * running task, so this is a launcher-owned recents list: every app opened
 * from here is recorded, shown as a card, and can be reopened or dismissed.
 * Dismissing sends `killBackgroundProcesses` for the app's package, which the
 * platform honours for background processes - it cannot stop another app's
 * foreground activity, and no launcher can.
 */
@Composable
fun RecentsScreen(
    onOpen: (RecentEntry) -> Unit,
    onHome: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val recents = RecentTasks.items

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.86f))
    ) {
        IosStatusBar()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Recent Apps",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            if (recents.isNotEmpty()) {
                Text(
                    "Clear All",
                    color = IosColors.Blue,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { RecentTasks.clear(context) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        if (recents.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No Recent Apps", color = IosColors.SecondaryLabel, fontSize = 17.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Apps you open from this launcher appear here.",
                        color = IosColors.TertiaryLabel,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(recents, key = { it.key }) { entry ->
                    RecentCard(
                        entry = entry,
                        onOpen = { onOpen(entry) },
                        onDismiss = { RecentTasks.killBackground(context, entry.key) }
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(width = 132.dp, height = 5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.85f))
                    .clickable { onHome() }
            )
        }
        Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun RecentCard(
    entry: RecentEntry,
    onOpen: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val builtin = builtinById(entry.key)
    val label = builtin?.name ?: entry.label

    Column(
        modifier = Modifier
            .width(196.dp)
            .pointerInput(entry.key) {
                detectHorizontalDragGestures { _, drag ->
                    if (drag < -30f) onDismiss()
                }
            }
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.14f))
            .clickable { onOpen() }
            .padding(12.dp)
    ) {
        // Faux app preview: app-coloured header with the icon centred.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (builtin != null) builtin.color.copy(alpha = 0.28f)
                    else Color.White.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (builtin != null) {
                GlyphIcon(builtin.glyph, builtin.color, 62.dp)
            } else {
                val app = AppRepository.apps(context).firstOrNull { it.key == entry.key }
                if (app != null) {
                    AppIconView(app = app, size = 62.dp)
                } else {
                    Text(
                        label.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                label,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Text("\u00D7", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

package com.ioslauncher.app.ui.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.ui.theme.IosColors
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** iOS status bar: time on the left, signal / wifi / battery on the right. */
@Composable
fun IosStatusBar(
    modifier: Modifier = Modifier,
    lightContent: Boolean = true
) {
    val context = LocalContext.current
    var time by remember { mutableStateOf(currentTime()) }
    var battery by remember { mutableIntStateOf(100) }
    var charging by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            time = currentTime()
            delay(15_000L)
        }
    }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
                if (level >= 0 && scale > 0) battery = level * 100 / scale
                val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL
            }
        }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        onDispose { runCatching { context.unregisterReceiver(receiver) } }
    }

    val tint = if (lightContent) Color.White else Color.Black

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 26.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = time,
            color = tint,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Cellular bars
            Canvas(modifier = Modifier.size(width = 18.dp, height = 12.dp)) {
                val barW = size.width / 5.2f
                val heights = listOf(0.35f, 0.55f, 0.75f, 1f)
                heights.forEachIndexed { i, h ->
                    drawRoundRect(
                        color = tint,
                        topLeft = Offset(i * (barW + 1.2f), size.height * (1f - h)),
                        size = Size(barW, size.height * h),
                        cornerRadius = CornerRadius(1.2f, 1.2f)
                    )
                }
            }
            Spacer(Modifier.width(6.dp))
            // Wi-Fi
            Canvas(modifier = Modifier.size(width = 16.dp, height = 12.dp)) {
                val stroke = Stroke(width = 2.2f, cap = StrokeCap.Round)
                val cx = size.width / 2f
                for (i in 0..2) {
                    val r = size.width * (0.22f + i * 0.26f)
                    drawArc(
                        color = tint,
                        startAngle = 225f,
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = Offset(cx - r, size.height * 0.62f - r),
                        size = Size(r * 2, r * 2),
                        style = stroke
                    )
                }
                drawCircle(color = tint, radius = 1.6f, center = Offset(cx, size.height * 0.72f))
            }
            Spacer(Modifier.width(6.dp))
            // Battery
            Canvas(modifier = Modifier.size(width = 26.dp, height = 13.dp)) {
                val bodyW = size.width - 3f
                val radius = CornerRadius(3.5f, 3.5f)
                drawRoundRect(
                    color = tint.copy(alpha = 0.45f),
                    topLeft = Offset(0f, 0f),
                    size = Size(bodyW, size.height),
                    cornerRadius = radius,
                    style = Stroke(width = 1.2f)
                )
                val inset = 2.2f
                val fillW = (bodyW - inset * 2) * (battery / 100f)
                drawRoundRect(
                    color = if (battery <= 20 && !charging) IosColors.Red else tint,
                    topLeft = Offset(inset, inset),
                    size = Size(fillW.coerceAtLeast(0f), size.height - inset * 2),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                drawRoundRect(
                    color = tint.copy(alpha = 0.45f),
                    topLeft = Offset(bodyW + 1.2f, size.height * 0.3f),
                    size = Size(1.8f, size.height * 0.4f),
                    cornerRadius = CornerRadius(1f, 1f)
                )
                if (charging) {
                    val p = Path().apply {
                        moveTo(size.width * 0.42f, size.height * 0.18f)
                        lineTo(size.width * 0.28f, size.height * 0.55f)
                        lineTo(size.width * 0.44f, size.height * 0.55f)
                        lineTo(size.width * 0.36f, size.height * 0.85f)
                        lineTo(size.width * 0.58f, size.height * 0.42f)
                        lineTo(size.width * 0.42f, size.height * 0.42f)
                        close()
                    }
                    drawPath(p, Color.Black.copy(alpha = 0.75f))
                }
            }
        }
    }
}

private fun currentTime(): String =
    SimpleDateFormat("h:mm", Locale.getDefault()).format(Date())

package com.ioslauncher.app.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.ui.components.IosTopBar
import com.ioslauncher.app.ui.theme.IosColors
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** iOS Clock: world clock, alarms, stopwatch, timer tabs. */
@Composable
fun ClockApp(onBack: () -> Unit) {
    var tab by remember { mutableStateOf(0) }
    var now by remember { mutableStateOf(Date()) }
    var stopwatchMs by remember { mutableStateOf(0L) }
    var running by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            if (running) stopwatchMs += 100
            delay(100L)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        IosTopBar(title = "Clock", onBack = onBack, large = true)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .padding(3.dp)
        ) {
            listOf("World", "Alarms", "Stopwatch", "Timer").forEachIndexed { i, label ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (tab == i) Color.White.copy(alpha = 0.22f) else Color.Transparent)
                        .clickable { tab = i }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        color = if (tab == i) Color.White else IosColors.SecondaryLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        when (tab) {
            0 -> Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    SimpleDateFormat("h:mm:ss a", Locale.getDefault()).format(now),
                    color = Color.White,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Thin
                )
                Text(
                    SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(now),
                    color = IosColors.SecondaryLabel,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(24.dp))
                listOf(
                    "London" to "GMT",
                    "New York" to "EST",
                    "Tokyo" to "JST",
                    "Dubai" to "GST"
                ).forEach { (city, zone) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(city, color = Color.White, fontSize = 17.sp)
                            Text(zone, color = IosColors.SecondaryLabel, fontSize = 12.sp)
                        }
                        Text(
                            SimpleDateFormat("h:mm", Locale.getDefault()).format(now),
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }

            1 -> Column(modifier = Modifier.padding(20.dp)) {
                listOf("6:30 AM" to "Weekdays", "7:00 AM" to "Every day", "9:15 PM" to "Mon, Wed, Fri")
                    .forEach { (time, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(time, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Light)
                                Text(label, color = IosColors.SecondaryLabel, fontSize = 12.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .size(width = 46.dp, height = 28.dp)
                                    .clip(CircleShape)
                                    .background(IosColors.Green.copy(alpha = 0.85f))
                            )
                        }
                    }
            }

            2 -> Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(40.dp))
                val mins = stopwatchMs / 60000
                val secs = (stopwatchMs / 1000) % 60
                val tenths = (stopwatchMs / 100) % 10
                Text(
                    String.format(Locale.US, "%02d:%02d.%d", mins, secs, tenths),
                    color = Color.White,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Thin
                )
                Spacer(Modifier.height(30.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(30.dp)) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.16f))
                            .clickable {
                                stopwatchMs = 0L
                                running = false
                            },
                        contentAlignment = Alignment.Center
                    ) { Text("Reset", color = Color.White, fontSize = 15.sp) }
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(if (running) IosColors.Red else IosColors.Green)
                            .clickable { running = !running },
                        contentAlignment = Alignment.Center
                    ) { Text(if (running) "Stop" else "Start", color = Color.White, fontSize = 15.sp) }
                }
            }

            else -> Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(40.dp))
                Text("00:05:00", color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.Thin)
                Spacer(Modifier.height(20.dp))
                Text(
                    "Timer presets",
                    color = IosColors.SecondaryLabel,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("1m", "5m", "10m", "30m").forEach { p ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.14f))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) { Text(p, color = Color.White, fontSize = 14.sp) }
                    }
                }
            }
        }
    }
}

package com.ioslauncher.app.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.ui.components.IosTopBar
import com.ioslauncher.app.ui.theme.IosColors

private data class DayForecast(val day: String, val glyph: String, val low: Int, val high: Int)

private val week = listOf(
    DayForecast("Today", "\u2600", 24, 32),
    DayForecast("Sat", "\u26C5", 25, 33),
    DayForecast("Sun", "\u2601", 24, 31),
    DayForecast("Mon", "\u2614", 23, 29),
    DayForecast("Tue", "\u26C8", 22, 28),
    DayForecast("Wed", "\u26C5", 23, 30),
    DayForecast("Thu", "\u2600", 24, 32)
)

/** iOS Weather: big temperature, hourly strip, 7-day list. */
@Composable
fun WeatherApp(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1B4B8F), Color(0xFF2E6FB7), Color(0xFF0B1B33))
                )
            )
    ) {
        IosTopBar(title = "Weather", onBack = onBack)

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Dhaka", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Medium)
            Text("32\u00B0", color = Color.White, fontSize = 88.sp, fontWeight = FontWeight.Thin)
            Text("Sunny", color = Color.White.copy(alpha = 0.9f), fontSize = 18.sp)
            Text(
                "H:33\u00B0  L:24\u00B0",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 15.sp
            )
        }

        Spacer(Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White.copy(alpha = 0.16f))
                .padding(vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Now" to 32, "1PM" to 33, "2PM" to 33, "3PM" to 32, "4PM" to 31)
                    .forEach { (label, temp) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(label, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                            Spacer(Modifier.height(6.dp))
                            Text("\u2600", color = IosColors.Yellow, fontSize = 18.sp)
                            Spacer(Modifier.height(6.dp))
                            Text("$temp\u00B0", color = Color.White, fontSize = 15.sp)
                        }
                    }
            }
        }

        Spacer(Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White.copy(alpha = 0.16f))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            week.forEach { d ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        d.day,
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(d.glyph, color = IosColors.Yellow, fontSize = 16.sp)
                    Spacer(Modifier.padding(horizontal = 10.dp))
                    Text(
                        "${d.low}\u00B0",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 15.sp
                    )
                    Spacer(Modifier.padding(horizontal = 6.dp))
                    Text("${d.high}\u00B0", color = Color.White, fontSize = 15.sp)
                }
            }
        }
    }
}

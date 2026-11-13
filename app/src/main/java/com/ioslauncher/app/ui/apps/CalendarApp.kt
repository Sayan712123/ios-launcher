package com.ioslauncher.app.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.ui.components.IosTopBar
import com.ioslauncher.app.ui.theme.IosColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** iOS Calendar: month grid with today highlighted, plus today's events. */
@Composable
fun CalendarApp(onBack: () -> Unit) {
    val cal = remember { Calendar.getInstance() }
    val today = remember { Calendar.getInstance() }
    val monthLabel = remember {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
    }
    val daysInMonth = remember { cal.getActualMaximum(Calendar.DAY_OF_MONTH) }
    val firstDayOfWeek = remember {
        val c = cal.clone() as Calendar
        c.set(Calendar.DAY_OF_MONTH, 1)
        c.get(Calendar.DAY_OF_WEEK) - 1
    }
    val todayDay = today.get(Calendar.DAY_OF_MONTH)
    val sameMonth = today.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
        today.get(Calendar.YEAR) == cal.get(Calendar.YEAR)

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        IosTopBar(title = "Calendar", onBack = onBack, large = true)

        Text(
            monthLabel,
            color = IosColors.Red,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { d ->
                Text(
                    d,
                    color = IosColors.SecondaryLabel,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
            var day = 1
            val cells = firstDayOfWeek + daysInMonth
            val rows = (cells + 6) / 7
            repeat(rows) { r ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(7) { c ->
                        val index = r * 7 + c
                        Box(
                            modifier = Modifier.weight(1f).aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (index >= firstDayOfWeek && day <= daysInMonth) {
                                val isToday = sameMonth && day == todayDay
                                Box(
                                    modifier = Modifier
                                        .height(34.dp)
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(if (isToday) IosColors.Red else Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        day.toString(),
                                        color = if (isToday) Color.White else Color.White.copy(alpha = 0.9f),
                                        fontSize = 15.sp,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                                day++
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(
            "Today",
            color = IosColors.SecondaryLabel,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(Modifier.height(6.dp))

        listOf(
            Triple("09:30", "Standup", IosColors.Blue),
            Triple("13:00", "Lunch with Rakib", IosColors.Green),
            Triple("16:00", "Code review", IosColors.Orange)
        ).forEach { (time, title, color) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(IosColors.Card)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .padding(end = 10.dp)
                        .background(color)
                        .padding(horizontal = 2.dp)
                )
                Column {
                    Text(title, color = Color.White, fontSize = 15.sp)
                    Text(time, color = IosColors.SecondaryLabel, fontSize = 12.sp)
                }
            }
        }
    }
}

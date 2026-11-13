package com.ioslauncher.app.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.ui.components.IosTopBar
import com.ioslauncher.app.ui.theme.IosColors

private data class Track(val title: String, val artist: String, val colors: List<Color>)

private val sampleTracks = listOf(
    Track("Midnight Drive", "Neon Coast", listOf(Color(0xFF2B1055), Color(0xFFBF5AF2))),
    Track("Paper Planes", "Aria", listOf(Color(0xFF0A4D68), Color(0xFF64D2FF))),
    Track("Golden Hour", "Sundown", listOf(Color(0xFFFF9F0A), Color(0xFFFF375F))),
    Track("Slow Motion", "Kite", listOf(Color(0xFF30D158), Color(0xFF0A84FF))),
    Track("Nightfall", "Echo", listOf(Color(0xFF1C1C1E), Color(0xFF5E5CE6)))
)

/** iOS Music: now-playing card, transport controls, up-next list. */
@Composable
fun MusicApp(onBack: () -> Unit) {
    var index by remember { mutableStateOf(0) }
    var playing by remember { mutableStateOf(false) }
    val track = sampleTracks[index]

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        IosTopBar(title = "Music", onBack = onBack)

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(track.colors)),
                contentAlignment = Alignment.Center
            ) {
                Text("\u266A", color = Color.White.copy(alpha = 0.5f), fontSize = 72.sp)
            }
            Spacer(Modifier.height(18.dp))
            Text(
                track.title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(track.artist, color = IosColors.SecondaryLabel, fontSize = 15.sp)
            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.35f)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("1:12", color = IosColors.SecondaryLabel, fontSize = 11.sp)
                Text("-2:04", color = IosColors.SecondaryLabel, fontSize = 11.sp)
            }

            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "\u23EE",
                    color = Color.White,
                    fontSize = 26.sp,
                    modifier = Modifier.clickable {
                        index = (index - 1 + sampleTracks.size) % sampleTracks.size
                    }
                )
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.16f))
                        .clickable { playing = !playing },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (playing) "\u23F8" else "\u25B6",
                        color = Color.White,
                        fontSize = 26.sp
                    )
                }
                Text(
                    "\u23ED",
                    color = Color.White,
                    fontSize = 26.sp,
                    modifier = Modifier.clickable {
                        index = (index + 1) % sampleTracks.size
                    }
                )
            }
        }

        Spacer(Modifier.height(18.dp))
        Text(
            "Up Next",
            color = IosColors.SecondaryLabel,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(Modifier.height(6.dp))

        sampleTracks.forEachIndexed { i, t ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { index = i }
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.linearGradient(t.colors))
                )
                Spacer(Modifier.padding(horizontal = 6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        t.title,
                        color = if (i == index) IosColors.Pink else Color.White,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(t.artist, color = IosColors.SecondaryLabel, fontSize = 12.sp)
                }
            }
        }
    }
}

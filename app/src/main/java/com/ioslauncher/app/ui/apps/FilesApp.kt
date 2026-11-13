package com.ioslauncher.app.ui.apps

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.ui.components.IosTopBar
import com.ioslauncher.app.ui.theme.IosColors

private data class FileEntry(val name: String, val size: String, val glyph: String, val color: Color)

private val sampleFiles = listOf(
    FileEntry("Downloads", "12 items", "\u2193", IosColors.Blue),
    FileEntry("Documents", "8 items", "\u25A4", IosColors.Blue),
    FileEntry("Pictures", "142 items", "\u2740", IosColors.Orange),
    FileEntry("Music", "36 items", "\u266A", IosColors.Pink),
    FileEntry("Videos", "9 items", "\u25B6", IosColors.Purple),
    FileEntry("build.zip", "24.6 MB", "\u25A4", IosColors.Gray),
    FileEntry("notes.txt", "3 KB", "\u25A4", IosColors.Gray),
    FileEntry("photo.jpg", "2.1 MB", "\u2740", IosColors.Gray)
)

/** iOS Files: Browse / Recents, grouped list with coloured glyphs. */
@Composable
fun FilesApp(onBack: () -> Unit) {
    var tab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        IosTopBar(title = "Files", onBack = onBack, large = true)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .padding(3.dp)
        ) {
            listOf("Recents", "Browse").forEachIndexed { i, label ->
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
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(sampleFiles, key = { it.name }) { f ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .padding(vertical = 10.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(f.color.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(f.glyph, color = Color.White, fontSize = 16.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(f.name, color = Color.White, fontSize = 16.sp)
                        Text(f.size, color = IosColors.SecondaryLabel, fontSize = 12.sp)
                    }
                    Text("\u203A", color = IosColors.TertiaryLabel, fontSize = 18.sp)
                }
            }
        }
    }
}

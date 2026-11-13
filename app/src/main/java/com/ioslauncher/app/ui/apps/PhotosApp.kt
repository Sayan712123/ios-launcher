package com.ioslauncher.app.ui.apps

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.ui.components.IosTopBar
import com.ioslauncher.app.ui.theme.IosColors

private val tilePalettes = listOf(
    listOf(Color(0xFF2B1055), Color(0xFF7597DE)),
    listOf(Color(0xFFE8A87C), Color(0xFFFF6B6B)),
    listOf(Color(0xFF0A4D68), Color(0xFF088395)),
    listOf(Color(0xFF3B1E63), Color(0xFFBF5AF2)),
    listOf(Color(0xFF1B2A6B), Color(0xFF64D2FF)),
    listOf(Color(0xFF2C2C34), Color(0xFF8E8E93)),
    listOf(Color(0xFF30D158), Color(0xFF0A84FF)),
    listOf(Color(0xFFFF9F0A), Color(0xFFFFD60A)),
    listOf(Color(0xFFFF375F), Color(0xFFBF5AF2)),
    listOf(Color(0xFF001B2E), Color(0xFF0A84FF)),
    listOf(Color(0xFF636366), Color(0xFF1C1C1E)),
    listOf(Color(0xFF5E5CE6), Color(0xFF64D2FF))
)

/** iOS Photos: Library / Albums tabs, gradient mosaic, opens the gallery. */
@Composable
fun PhotosApp(onBack: () -> Unit) {
    val context = LocalContext.current
    var tab by remember { mutableStateOf(0) }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_VIEW)
            .setType("image/*")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        IosTopBar(title = "Photos", onBack = onBack, large = true)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .padding(3.dp)
        ) {
            listOf("Library", "Albums").forEachIndexed { i, label ->
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

        if (tab == 0) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(tilePalettes.indices.toList()) { i ->
                    val p = tilePalettes[i]
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(Brush.linearGradient(p))
                            .clickable { openGallery() }
                    )
                }
            }
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                listOf(
                    "Recents" to "${tilePalettes.size}",
                    "Favourites" to "4",
                    "Screenshots" to "12",
                    "Videos" to "3"
                ).forEach { (name, count) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { openGallery() }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .height(52.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        tilePalettes[name.length % tilePalettes.size]
                                    )
                                )
                        )
                        Spacer(Modifier.padding(horizontal = 6.dp))
                        Column {
                            Text(name, color = Color.White, fontSize = 16.sp)
                            Text(count, color = IosColors.SecondaryLabel, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

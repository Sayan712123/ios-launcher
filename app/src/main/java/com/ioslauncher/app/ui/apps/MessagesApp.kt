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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.ui.components.IosTopBar
import com.ioslauncher.app.ui.theme.IosColors

private data class Thread(val name: String, val preview: String, val time: String, val unread: Boolean)

private val sampleThreads = listOf(
    Thread("Ayesha", "See you at 7 then \uD83D\uDC4D", "9:41", true),
    Thread("Rakib", "Sent the build link, check it", "8:12", true),
    Thread("Family", "Mom: dinner on Sunday?", "Yesterday", false),
    Thread("Bank", "Your OTP is 482913", "Yesterday", false),
    Thread("Nadia", "Thanks for the help!", "Mon", false)
)

/** iOS Messages: conversation list, tap opens the real SMS app. */
@Composable
fun MessagesApp(onBack: () -> Unit) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val threads = remember(query) {
        if (query.isBlank()) sampleThreads
        else sampleThreads.filter { it.name.contains(query, true) }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        IosTopBar(title = "Messages", onBack = onBack, large = true)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("\u2315", color = IosColors.SecondaryLabel, fontSize = 14.sp)
            Spacer(Modifier.width(6.dp))
            Box(Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text("Search", color = IosColors.SecondaryLabel, fontSize = 15.sp)
                }
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
                    cursorBrush = SolidColor(IosColors.Blue),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(threads, key = { it.name }) { t ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            val intent = Intent(Intent.ACTION_MAIN)
                                .addCategory(Intent.CATEGORY_APP_MESSAGING)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            runCatching { context.startActivity(intent) }
                        }
                        .padding(vertical = 10.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(CircleShape)
                            .background(IosColors.Green.copy(alpha = 0.75f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            t.name.first().uppercaseChar().toString(),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            t.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            t.preview,
                            color = IosColors.SecondaryLabel,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(t.time, color = IosColors.SecondaryLabel, fontSize = 12.sp)
                        if (t.unread) {
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier.size(9.dp).clip(CircleShape)
                                    .background(IosColors.Blue)
                            )
                        }
                    }
                }
            }
        }
    }
}

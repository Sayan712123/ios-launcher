package com.ioslauncher.app.ui.apps

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.data.LocalStore
import com.ioslauncher.app.ui.components.IosTopBar
import com.ioslauncher.app.ui.theme.IosColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** iOS Phone: keypad, call button, recent calls list. */
@Composable
fun PhoneApp(onBack: () -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { LocalStore.loadCalls(context) }

    var number by remember { mutableStateOf("") }
    var tab by remember { mutableStateOf(0) }

    fun dial() {
        if (number.isBlank()) return
        LocalStore.addCall(context, number, "Outgoing")
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        IosTopBar(title = "Phone", onBack = onBack)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .padding(3.dp)
        ) {
            listOf("Keypad", "Recents").forEachIndexed { i, label ->
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
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(10.dp))
                Text(
                    number.ifBlank { " " },
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(14.dp))

                val keys = listOf(
                    listOf("1" to "", "2" to "ABC", "3" to "DEF"),
                    listOf("4" to "GHI", "5" to "JKL", "6" to "MNO"),
                    listOf("7" to "PQRS", "8" to "TUV", "9" to "WXYZ"),
                    listOf("*" to "", "0" to "+", "#" to "")
                )
                keys.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                        row.forEach { (digit, letters) ->
                            Column(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.14f))
                                    .clickable { number += digit }
                                    .padding(top = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(digit, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Light)
                                if (letters.isNotEmpty()) {
                                    Text(letters, color = Color.White, fontSize = 9.sp, letterSpacing = 1.sp)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.size(56.dp))
                    Box(
                        modifier = Modifier
                            .size(66.dp)
                            .clip(CircleShape)
                            .background(IosColors.Green)
                            .clickable { dial() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("\u2706", color = Color.White, fontSize = 28.sp)
                    }
                    Text(
                        "\u232B",
                        color = Color.White,
                        fontSize = 22.sp,
                        modifier = Modifier
                            .size(56.dp)
                            .clickable {
                                if (number.isNotEmpty()) number = number.dropLast(1)
                            }
                            .padding(14.dp)
                    )
                }
            }
        } else {
            if (LocalStore.callLog.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No Recent Calls", color = IosColors.SecondaryLabel, fontSize = 15.sp)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(LocalStore.callLog, key = { "${it.number}-${it.at}" }) { call ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${call.number}"))
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    runCatching { context.startActivity(intent) }
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "\u2197",
                                color = IosColors.Green,
                                fontSize = 15.sp,
                                modifier = Modifier.width(24.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(call.number, color = Color.White, fontSize = 16.sp)
                                Text(
                                    call.type,
                                    color = IosColors.SecondaryLabel,
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                SimpleDateFormat("h:mm a", Locale.getDefault())
                                    .format(Date(call.at)),
                                color = IosColors.SecondaryLabel,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

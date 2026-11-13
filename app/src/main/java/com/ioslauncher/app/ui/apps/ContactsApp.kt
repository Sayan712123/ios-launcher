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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.ui.components.IosTopBar
import com.ioslauncher.app.ui.theme.IosColors

private data class Contact(val name: String, val number: String)

private val sampleContacts = listOf(
    Contact("Ayesha Rahman", "+8801711000001"),
    Contact("Rakib Hasan", "+8801711000002"),
    Contact("Nadia Islam", "+8801711000003"),
    Contact("Tanvir Ahmed", "+8801711000004"),
    Contact("Sadia Chowdhury", "+8801711000005"),
    Contact("Imran Khan", "+8801711000006"),
    Contact("Farhana Akter", "+8801711000007"),
    Contact("Mehedi Hasan", "+8801711000008")
)

/** iOS Contacts: A-Z list with call / message actions. */
@Composable
fun ContactsApp(onBack: () -> Unit) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        if (query.isBlank()) sampleContacts
        else sampleContacts.filter { it.name.contains(query, true) }
    }
    val grouped = remember(filtered) {
        filtered.groupBy { it.name.first().uppercaseChar().toString() }.toSortedMap()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        IosTopBar(title = "Contacts", onBack = onBack, large = true)

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
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            grouped.forEach { (letter, people) ->
                item(key = "hdr:$letter") {
                    Text(
                        letter,
                        color = IosColors.SecondaryLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 6.dp, top = 10.dp, bottom = 4.dp)
                    )
                }
                items(people, key = { it.number }) { c ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${c.number}"))
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                runCatching { context.startActivity(intent) }
                            }
                            .padding(vertical = 9.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(CircleShape)
                                .background(IosColors.Gray.copy(alpha = 0.55f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                c.name.first().uppercaseChar().toString(),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(c.name, color = Color.White, fontSize = 16.sp)
                            Text(c.number, color = IosColors.SecondaryLabel, fontSize = 12.sp)
                        }
                        Text("\u2706", color = IosColors.Green, fontSize = 17.sp)
                    }
                }
            }
        }
    }
}

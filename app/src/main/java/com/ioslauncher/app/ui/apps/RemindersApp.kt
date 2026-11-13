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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.data.LocalStore
import com.ioslauncher.app.ui.components.IosEmptyState
import com.ioslauncher.app.ui.components.IosTopBar
import com.ioslauncher.app.ui.theme.IosColors

/** iOS Reminders: orange accent, tap circle to complete. */
@Composable
fun RemindersApp(onBack: () -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { LocalStore.loadReminders(context) }
    var draft by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        IosTopBar(title = "Reminders", onBack = onBack, large = true)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(IosColors.Card)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("+", color = IosColors.Orange, fontSize = 20.sp)
            Spacer(Modifier.width(10.dp))
            Box(Modifier.weight(1f)) {
                if (draft.isEmpty()) {
                    Text("New Reminder", color = IosColors.SecondaryLabel, fontSize = 15.sp)
                }
                BasicTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
                    cursorBrush = SolidColor(IosColors.Orange),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Text(
                "Add",
                color = IosColors.Orange,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable {
                        LocalStore.addReminder(context, draft)
                        draft = ""
                    }
                    .padding(4.dp)
            )
        }

        if (LocalStore.reminders.isEmpty()) {
            IosEmptyState("No Reminders", "Add one above")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(LocalStore.reminders, key = { it.id }) { r ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(
                                    if (r.done) IosColors.Orange else Color.Transparent
                                )
                                .clickable { LocalStore.toggleReminder(context, r.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (r.done) {
                                Text("\u2713", color = Color.White, fontSize = 13.sp)
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.25f))
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            r.title,
                            color = if (r.done) IosColors.SecondaryLabel else Color.White,
                            fontSize = 16.sp,
                            textDecoration = if (r.done) TextDecoration.LineThrough else null,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "\u2715",
                            color = IosColors.TertiaryLabel,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable { LocalStore.deleteReminder(context, r.id) }
                                .padding(6.dp)
                        )
                    }
                }
            }
        }
    }
}

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.data.LocalStore
import com.ioslauncher.app.data.Note
import com.ioslauncher.app.ui.components.IosEmptyState
import com.ioslauncher.app.ui.components.IosTopBar
import com.ioslauncher.app.ui.theme.IosColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** iOS Notes: yellow accent, list of notes, tap to edit inline. */
@Composable
fun NotesApp(onBack: () -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { LocalStore.loadNotes(context) }

    var editing by remember { mutableStateOf<Note?>(null) }
    var draft by remember { mutableStateOf("") }

    if (editing != null) {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            IosTopBar(
                title = "Note",
                onBack = {
                    LocalStore.updateNote(context, editing!!.id, draft)
                    editing = null
                },
                trailing = {
                    Text(
                        "Done",
                        color = IosColors.Blue,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clickable {
                                LocalStore.updateNote(context, editing!!.id, draft)
                                editing = null
                            }
                            .padding(6.dp)
                    )
                }
            )
            BasicTextField(
                value = draft,
                onValueChange = { draft = it },
                textStyle = TextStyle(color = Color.White, fontSize = 17.sp),
                cursorBrush = SolidColor(IosColors.Yellow),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            )
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        IosTopBar(
            title = "Notes",
            onBack = onBack,
            large = true,
            trailing = {
                Text(
                    "\u270E",
                    color = IosColors.Yellow,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable {
                            val n = LocalStore.addNote(context, "")
                            editing = n
                            draft = ""
                        }
                        .padding(6.dp)
                )
            }
        )

        if (LocalStore.notes.isEmpty()) {
            IosEmptyState("No Notes", "Tap the pencil to write one")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(LocalStore.notes, key = { it.id }) { note ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(IosColors.Card)
                            .clickable {
                                editing = note
                                draft = note.title
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                note.title.ifBlank { "New Note" },
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                SimpleDateFormat("d MMM, h:mm a", Locale.getDefault())
                                    .format(Date(note.updatedAt)),
                                color = IosColors.SecondaryLabel,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "\u2715",
                            color = IosColors.TertiaryLabel,
                            fontSize = 15.sp,
                            modifier = Modifier
                                .clickable { LocalStore.deleteNote(context, note.id) }
                                .padding(6.dp)
                        )
                    }
                }
            }
        }
    }
}

package com.ioslauncher.app.ui

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
import com.ioslauncher.app.data.AppLauncher
import com.ioslauncher.app.ui.components.AppIconView
import com.ioslauncher.app.ui.components.GlyphIcon
import com.ioslauncher.app.ui.components.IosStatusBar
import com.ioslauncher.app.ui.theme.IosColors
import com.ioslauncher.app.util.SystemUtils

/** iOS Spotlight-style search over built-in and installed apps. */
@Composable
fun SearchScreen(
    vm: LauncherViewModel,
    onBack: () -> Unit,
    onOpenBuiltin: (String) -> Unit
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }

    val builtinMatches = remember(query) {
        if (query.isBlank()) emptyList()
        else builtinApps.filter { it.name.contains(query, ignoreCase = true) }
    }
    val appMatches = remember(query, vm.apps) {
        if (query.isBlank()) emptyList()
        else vm.apps.filter { it.label.contains(query, ignoreCase = true) }.take(30)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        IosStatusBar()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Cancel",
                color = IosColors.Blue,
                fontSize = 16.sp,
                modifier = Modifier.clickable { onBack() }
            )
            Spacer(Modifier.width(12.dp))
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("\u2315", color = IosColors.SecondaryLabel, fontSize = 15.sp)
                Spacer(Modifier.width(6.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text("Search", color = IosColors.SecondaryLabel, fontSize = 16.sp)
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                        cursorBrush = SolidColor(IosColors.Blue),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (query.isBlank()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Siri Suggestions",
                    color = IosColors.SecondaryLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    builtinApps.take(4).forEach { app ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onOpenBuiltin(app.id) }
                        ) {
                            GlyphIcon(app.glyph, app.color, 56.dp)
                            Spacer(Modifier.height(4.dp))
                            Text(app.name, color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(builtinMatches, key = { "b:${it.id}" }) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onOpenBuiltin(app.id) }
                            .padding(vertical = 8.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlyphIcon(app.glyph, app.color, 40.dp)
                        Spacer(Modifier.width(12.dp))
                        Text(app.name, color = Color.White, fontSize = 16.sp)
                    }
                }
                items(appMatches, key = { it.key }) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (!AppLauncher.openInstalled(context, app)) {
                                    SystemUtils.openAppInfo(context, app.packageName)
                                }
                            }
                            .padding(vertical = 8.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconView(app = app, size = 40.dp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                app.label,
                                color = Color.White,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                app.packageName,
                                color = IosColors.SecondaryLabel,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
                if (builtinMatches.isEmpty() && appMatches.isEmpty()) {
                    item {
                        Text(
                            "No Results",
                            color = IosColors.SecondaryLabel,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            }
        }
    }
}

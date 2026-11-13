package com.ioslauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.data.AppLauncher
import com.ioslauncher.app.ui.components.AppIconView
import com.ioslauncher.app.ui.components.GlyphIcon
import com.ioslauncher.app.ui.components.IosStatusBar
import com.ioslauncher.app.ui.components.IosTopBar
import com.ioslauncher.app.ui.theme.IosColors
import com.ioslauncher.app.util.SystemUtils

/** iOS App Library: built-in apps first, then every installed app A-Z. */
@Composable
fun AppLibraryScreen(
    vm: LauncherViewModel,
    onBack: () -> Unit,
    onOpenBuiltin: (String) -> Unit
) {
    val context = LocalContext.current
    val installed = vm.apps
    val grouped = remember(installed) {
        installed.groupBy { app ->
            app.label.firstOrNull()?.uppercaseChar()?.toString() ?: "#"
        }.toSortedMap()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        IosStatusBar()
        IosTopBar(title = "App Library", onBack = onBack)

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { GridItemSpan(4) }) {
                Text(
                    "Built-in",
                    color = IosColors.SecondaryLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                )
            }
            items(builtinApps, key = { "builtin:${it.id}" }) { app ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenBuiltin(app.id) }
                ) {
                    GlyphIcon(glyph = app.glyph, color = app.color, size = 60.dp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        app.name,
                        color = Color.White,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (installed.isNotEmpty()) {
                item(span = { GridItemSpan(4) }) {
                    Text(
                        "Installed",
                        color = IosColors.SecondaryLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 18.dp, bottom = 2.dp)
                    )
                }
            }

            grouped.forEach { (letter, apps) ->
                item(span = { GridItemSpan(4) }, key = "hdr:$letter") {
                    Text(
                        letter,
                        color = IosColors.SecondaryLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
                items(apps, key = { it.key }) { app ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                if (!AppLauncher.openInstalled(context, app)) {
                                    SystemUtils.openAppInfo(context, app.packageName)
                                }
                            }
                    ) {
                        AppIconView(app = app, size = 60.dp)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            app.label,
                            color = Color.White,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

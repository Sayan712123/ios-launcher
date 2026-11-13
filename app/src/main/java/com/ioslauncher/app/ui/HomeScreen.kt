package com.ioslauncher.app.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.data.AppInfo
import com.ioslauncher.app.data.AppLauncher
import com.ioslauncher.app.data.Folder
import com.ioslauncher.app.ui.components.AppIconView
import com.ioslauncher.app.ui.components.GlyphIcon
import com.ioslauncher.app.ui.components.IosStatusBar
import com.ioslauncher.app.ui.theme.IosColors
import com.ioslauncher.app.util.SystemUtils

private const val PER_PAGE = 24

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    vm: LauncherViewModel,
    onOpenBuiltin: (String) -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenFolder: (String) -> Unit
) {
    val context = LocalContext.current
    val items = vm.homeItems
    val pages = remember(items) {
        if (items.isEmpty()) listOf(emptyList()) else items.chunked(PER_PAGE)
    }
    val pagerState = rememberPagerState(pageCount = { pages.size })
    var contextMenuApp by remember { mutableStateOf<AppInfo?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        IosStatusBar()

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val pageItems = pages.getOrElse(page) { emptyList() }
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(pageItems, key = { item ->
                    when (item) {
                        is HomeItem.App -> item.app.key
                        is HomeItem.FolderItem -> "folder:${item.folder.name}"
                        is HomeItem.Builtin -> "builtin:${item.id}"
                    }
                }) { item ->
                    when (item) {
                        is HomeItem.App -> AppCell(
                            app = item.app,
                            onClick = {
                                if (!AppLauncher.openInstalled(context, item.app)) {
                                    SystemUtils.openAppInfo(context, item.app.packageName)
                                }
                            },
                            onLongClick = { contextMenuApp = item.app }
                        )

                        is HomeItem.FolderItem -> FolderCell(
                            folder = item.folder,
                            vm = vm,
                            onClick = { onOpenFolder(item.folder.name) }
                        )

                        is HomeItem.Builtin -> {
                            val b = builtinById(item.id)
                            if (b != null) {
                                BuiltinCell(app = b, onClick = { onOpenBuiltin(b.id) })
                            }
                        }
                    }
                }
            }
        }

        if (pages.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pages.size) { i ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(
                                if (i == pagerState.currentPage) Color.White
                                else Color.White.copy(alpha = 0.35f)
                            )
                    )
                }
            }
        }

        // iOS-style search pill above the dock
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 60.dp)
                .padding(bottom = 10.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.18f))
                .clickable { onOpenSearch() }
                .padding(vertical = 7.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("\u2315", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
            Spacer(Modifier.width(5.dp))
            Text("Search", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
        }

        Dock(
            vm = vm,
            onOpenBuiltin = onOpenBuiltin,
            onOpenLibrary = onOpenLibrary,
            onOpenSearch = onOpenSearch
        )
    }

    contextMenuApp?.let { app ->
        AppContextMenu(
            app = app,
            onDismiss = { contextMenuApp = null },
            onAddToDock = { vm.addToDock(app); contextMenuApp = null },
            onHide = { vm.hide(app); contextMenuApp = null },
            onAppInfo = {
                SystemUtils.openAppInfo(context, app.packageName)
                contextMenuApp = null
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppCell(
    app: AppInfo,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(vertical = 2.dp)
    ) {
        AppIconView(app = app, size = 60.dp)
        Spacer(Modifier.height(6.dp))
        Text(
            text = app.label,
            color = Color.White,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun BuiltinCell(app: BuiltinApp, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 2.dp)
    ) {
        GlyphIcon(glyph = app.glyph, color = app.color, size = 60.dp)
        Spacer(Modifier.height(6.dp))
        Text(
            text = app.name,
            color = Color.White,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FolderCell(
    folder: Folder,
    vm: LauncherViewModel,
    onClick: () -> Unit
) {
    val preview = folder.appKeys.take(4).mapNotNull { vm.appByKey(it) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    preview.getOrNull(0)?.let { AppIconView(it, 22.dp) }
                    preview.getOrNull(1)?.let { AppIconView(it, 22.dp) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    preview.getOrNull(2)?.let { AppIconView(it, 22.dp) }
                    preview.getOrNull(3)?.let { AppIconView(it, 22.dp) }
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = folder.name,
            color = Color.White,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Dock(
    vm: LauncherViewModel,
    onOpenBuiltin: (String) -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenSearch: () -> Unit
) {
    val context = LocalContext.current
    val dockApps = vm.dockApps

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 18.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            dockApps.take(4).forEach { app ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            if (!AppLauncher.openInstalled(context, app)) {
                                SystemUtils.openAppInfo(context, app.packageName)
                            }
                        }
                ) {
                    AppIconView(app = app, size = 56.dp)
                }
            }
            if (dockApps.isEmpty()) {
                Text(
                    "No apps yet",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun AppContextMenu(
    app: AppInfo,
    onDismiss: () -> Unit,
    onAddToDock: () -> Unit,
    onHide: () -> Unit,
    onAppInfo: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(260.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(IosColors.Sheet)
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconView(app = app, size = 44.dp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        app.label,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        app.packageName,
                        color = IosColors.SecondaryLabel,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }
            MenuRow("Add to Dock", onAddToDock)
            MenuRow("Hide from Home Screen", onHide)
            MenuRow("App Info", onAppInfo)
        }
    }
}

@Composable
private fun MenuRow(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = IosColors.Blue,
        fontSize = 16.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp)
    )
}

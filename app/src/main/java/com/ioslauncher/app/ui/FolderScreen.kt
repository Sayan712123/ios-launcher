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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.ioslauncher.app.ui.components.IosStatusBar
import com.ioslauncher.app.ui.components.IosTopBar
import com.ioslauncher.app.util.SystemUtils

/** Full-screen folder, iOS style: dimmed backdrop, 4-column grid. */
@Composable
fun FolderScreen(
    vm: LauncherViewModel,
    folderName: String,
    onBack: () -> Unit,
    onOpenBuiltin: (String) -> Unit
) {
    val context = LocalContext.current
    val folder = vm.folders.firstOrNull { it.name == folderName }
    val apps = folder?.appKeys?.mapNotNull { vm.appByKey(it) } ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
    ) {
        IosStatusBar()
        IosTopBar(title = folderName, onBack = onBack)

        Text(
            text = folderName,
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 22.dp, top = 6.dp, bottom = 10.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxSize()
        ) {
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

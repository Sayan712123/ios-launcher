package com.ioslauncher.app.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.ui.LauncherViewModel
import com.ioslauncher.app.ui.components.IosTopBar
import com.ioslauncher.app.ui.theme.IosColors
import com.ioslauncher.app.util.SystemUtils

/** iOS Settings: grouped rows, launcher-specific options at the top. */
@Composable
fun SettingsApp(vm: LauncherViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var dark by remember { mutableStateOf(vm.darkWallpaper) }
    var style by remember { mutableStateOf(vm.wallpaperStyle) }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        IosTopBar(title = "Settings", onBack = onBack, large = true)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            GroupCard {
                SettingRow("\u2699", IosColors.Gray, "Set as default launcher") {
                    SystemUtils.openLauncherSettings(context)
                }
                Divider()
                SettingRow("\u25A6", IosColors.Red, "Notification access") {
                    SystemUtils.openNotificationAccessSettings(context)
                }
                Divider()
                SettingRow("\u2315", IosColors.Blue, "Search apps") { }
            }

            SectionLabel("Wallpaper")
            GroupCard {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    repeat(4) { i ->
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(wallpaperPreview(i))
                                .clickable {
                                    style = i
                                    vm.applyWallpaper(i)
                                }
                        ) {
                            if (style == i) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(3.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Transparent)
                                )
                            }
                        }
                    }
                }
                Divider()
                SettingRow("\u25D1", IosColors.Indigo, "Dark wallpaper") {
                    dark = !dark
                    vm.applyDarkWallpaper(dark)
                }
            }

            SectionLabel("Home Screen")
            GroupCard {
                SettingRow("\u21BA", IosColors.Green, "Show all hidden apps") {
                    vm.unhideAll()
                }
                Divider()
                SettingRow("\u2699", IosColors.Gray, "App info") {
                    SystemUtils.openAppInfo(context, context.packageName)
                }
            }

            SectionLabel("System")
            GroupCard {
                SettingRow("\u2248", IosColors.Blue, "Wi-Fi") { SystemUtils.openWifiSettings(context) }
                Divider()
                SettingRow("\u16D5", IosColors.Blue, "Bluetooth") {
                    SystemUtils.openBluetoothSettings(context)
                }
                Divider()
                SettingRow("\u2600", IosColors.Orange, "Display") {
                    SystemUtils.openDisplaySettings(context)
                }
                Divider()
                SettingRow("\u266A", IosColors.Pink, "Sounds") {
                    SystemUtils.openSoundSettings(context)
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "Launcher 1.0.0",
                color = IosColors.TertiaryLabel,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GroupCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(IosColors.Card)
    ) { content() }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 52.dp)
            .height(0.6.dp)
            .background(Color.White.copy(alpha = 0.12f))
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        color = IosColors.SecondaryLabel,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 6.dp, top = 18.dp, bottom = 6.dp)
    )
}

@Composable
private fun SettingRow(
    glyph: String,
    color: Color,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(glyph, color = Color.White, fontSize = 14.sp)
        }
        Spacer(Modifier.width(12.dp))
        Text(title, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Text("\u203A", color = IosColors.TertiaryLabel, fontSize = 18.sp)
    }
}

private fun wallpaperPreview(style: Int): Brush = when (style % 4) {
    0 -> Brush.linearGradient(listOf(Color(0xFF0B1026), Color(0xFF3B1E63)))
    1 -> Brush.linearGradient(listOf(Color(0xFF2B1055), Color(0xFFE8A87C)))
    2 -> Brush.linearGradient(listOf(Color(0xFF101014), Color(0xFF3A3A44)))
    else -> Brush.linearGradient(listOf(Color(0xFF001B2E), Color(0xFF088395)))
}

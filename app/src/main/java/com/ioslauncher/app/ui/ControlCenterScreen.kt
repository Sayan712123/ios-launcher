package com.ioslauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.ui.components.IosStatusBar
import com.ioslauncher.app.ui.theme.IosColors
import com.ioslauncher.app.util.SystemUtils

/** iOS Control Centre: connectivity block, media block, brightness/volume. */
@Composable
fun ControlCenterScreen(fakeBlur: Boolean = true, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var wifiOn by remember { mutableStateOf(true) }
    var btOn by remember { mutableStateOf(true) }
    var airplane by remember { mutableStateOf(false) }
    var flashlight by remember { mutableStateOf(false) }
    var brightness by remember { mutableFloatStateOf(0.7f) }
    var volume by remember { mutableFloatStateOf(0.5f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(
                // Cheap frosted-glass illusion. A real RenderEffect blur
                // re-samples the whole window every frame; on a POCO M4 that
                // drops frames, so this stacks translucent scrims instead.
                if (fakeBlur) {
                    Modifier.background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color(0xFF1C1C2E).copy(alpha = 0.78f),
                                Color.Black.copy(alpha = 0.9f)
                            )
                        )
                    )
                } else {
                    Modifier.background(Color.Black.copy(alpha = 0.82f))
                }
            )
            .padding(horizontal = 14.dp)
    ) {
        IosStatusBar()

        Spacer(Modifier.height(6.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1.05f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color.White.copy(alpha = 0.16f))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ToggleCircle("\u2708", airplane, IosColors.Orange) {
                            airplane = it
                            SystemUtils.openAirplaneSettings(context)
                        }
                        ToggleCircle("\u2248", wifiOn, IosColors.Blue) {
                            wifiOn = it
                            if (!it) SystemUtils.openWifiSettings(context)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ToggleCircle("\u16D5", btOn, IosColors.Blue) {
                            btOn = it
                            if (!it) SystemUtils.openBluetoothSettings(context)
                        }
                        ToggleCircle("\u2600", false, IosColors.Gray) {
                            SystemUtils.openDisplaySettings(context)
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1.05f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color.White.copy(alpha = 0.16f))
                    .padding(14.dp)
            ) {
                Column {
                    Text("Not Playing", color = IosColors.SecondaryLabel, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Music",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.weight(1f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("\u23EE", color = Color.White, fontSize = 20.sp)
                        Text("\u25B6", color = Color.White, fontSize = 22.sp)
                        Text("\u23ED", color = Color.White, fontSize = 20.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ToggleCircle("\u2600", flashlight, Color.White) { on ->
                flashlight = on
                if (!SystemUtils.setFlashlight(context, on)) flashlight = false
            }
            ToggleCircle("\u2699", false, IosColors.Gray) {
                SystemUtils.openLauncherSettings(context)
            }
            ToggleCircle("\u25A6", false, IosColors.Gray) {
                SystemUtils.openNotificationAccessSettings(context)
            }
            ToggleCircle("\u2318", false, IosColors.Gray) {
                SystemUtils.openSoundSettings(context)
            }
        }

        Spacer(Modifier.height(16.dp))

        SliderBlock("Brightness", brightness) { v ->
            brightness = v
            SystemUtils.setBrightness(context, v)
        }
        Spacer(Modifier.height(10.dp))
        SliderBlock("Volume", volume) { volume = it }

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 26.dp)
                .size(width = 120.dp, height = 5.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.5f))
                .clickable { onDismiss() }
        )
    }
}

@Composable
private fun ToggleCircle(
    glyph: String,
    active: Boolean,
    activeColor: Color,
    onToggle: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(if (active) activeColor else Color.White.copy(alpha = 0.18f))
            .clickable { onToggle(!active) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            glyph,
            color = if (active) Color.White else Color.White.copy(alpha = 0.9f),
            fontSize = 20.sp
        )
    }
}

@Composable
private fun SliderBlock(label: String, value: Float, onChange: (Float) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(label, color = IosColors.SecondaryLabel, fontSize = 11.sp)
        Slider(
            value = value,
            onValueChange = onChange,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.25f)
            )
        )
    }
}

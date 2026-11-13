package com.ioslauncher.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * The launcher's own navigation bar.
 *
 * Drawing it ourselves is what lets the phone's three-button bar disappear:
 * the activity goes edge-to-edge in immersive mode (see MainActivity), and this
 * strip takes over back / home / recents. Gesture style draws the iOS-style
 * pill and relies on edge-swipe detectors in LauncherRoot.
 */
@Composable
fun LauncherNavBar(
    style: Int,
    visible: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onRecents: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(if (style == 1) 26.dp else 46.dp)
            .padding(bottom = if (style == 1) 6.dp else 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (style == 1) {
            Arrangement.Center
        } else {
            Arrangement.SpaceEvenly
        }
    ) {
        if (style == 1) {
            // iOS home indicator / Android gesture pill.
            Box(
                modifier = Modifier
                    .size(width = 132.dp, height = 5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.85f))
                    .clickable { onHome() }
            )
        } else {
            NavButton(modifier = Modifier.size(46.dp), onClick = onBack) { c ->
                val p = Path().apply {
                    moveTo(c.x + 3f, c.y - 6f)
                    lineTo(c.x - 3f, c.y)
                    lineTo(c.x + 3f, c.y + 6f)
                }
                drawPath(
                    p,
                    Color.White,
                    style = Stroke(width = 2.2f, cap = StrokeCap.Round)
                )
            }
            NavButton(modifier = Modifier.size(46.dp), onClick = onHome) { c ->
                drawCircle(
                    color = Color.White,
                    radius = 7f,
                    center = c,
                    style = Stroke(width = 2.2f)
                )
            }
            NavButton(modifier = Modifier.size(46.dp), onClick = onRecents) { c ->
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(c.x - 6f, c.y - 6f),
                    size = Size(12f, 12f),
                    cornerRadius = CornerRadius(2.5f, 2.5f),
                    style = Stroke(width = 2.2f)
                )
            }
        }
    }
}

@Composable
private fun NavButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    draw: DrawScope.(Offset) -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(24.dp)) {
            draw(Offset(size.width / 2f, size.height / 2f))
        }
    }
}

/**
 * Cheap frosted-glass panel: layered translucent scrims instead of a real
 * blur. A RenderEffect blur would sample the whole window every frame.
 */
@Composable
fun FakeBlurPanel(
    modifier: Modifier = Modifier,
    alpha: Float = 0.62f,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = (alpha * 0.16f)))
            .background(Color.Black.copy(alpha = (alpha * 0.42f)))
    ) { content() }
}

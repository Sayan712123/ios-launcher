package com.ioslauncher.app.ui.apps

import android.content.Intent
import android.provider.MediaStore
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.ui.components.IosTopBar
import com.ioslauncher.app.ui.theme.IosColors

/**
 * iOS Camera shell. The viewfinder itself is the device camera app: a launcher
 * cannot legally hold a camera preview without its own CAMERA permission flow,
 * so the shutter hands off to the system camera and returns here.
 */
@Composable
fun CameraApp(onBack: () -> Unit) {
    val context = LocalContext.current
    var mode by remember { mutableStateOf("PHOTO") }

    fun shoot() {
        val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        IosTopBar(title = "Camera", onBack = onBack)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 10.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF111114))
                .clickable { shoot() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("\u25C9", color = Color.White.copy(alpha = 0.35f), fontSize = 64.sp)
                Spacer(Modifier.height(10.dp))
                Text(
                    "Tap to open the camera",
                    color = IosColors.SecondaryLabel,
                    fontSize = 14.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            listOf("VIDEO", "PHOTO", "PORTRAIT").forEach { m ->
                Text(
                    m,
                    color = if (mode == m) IosColors.Yellow else IosColors.SecondaryLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { mode = m }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 22.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.18f))
            )
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { shoot() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text("\u21BB", color = Color.White, fontSize = 20.sp)
            }
        }
    }
}

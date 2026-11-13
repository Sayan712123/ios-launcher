package com.ioslauncher.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioslauncher.app.ui.theme.IosColors

/** iOS navigation bar: centred title, blue back chevron on the left. */
@Composable
fun IosTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    large: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(72.dp)) {
                if (onBack != null) {
                    Text(
                        text = "\u2039  Back",
                        color = IosColors.Blue,
                        fontSize = 17.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onBack() }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }
            Text(
                text = title,
                color = IosColors.Label,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Box(modifier = Modifier.width(72.dp), contentAlignment = Alignment.CenterEnd) {
                trailing?.invoke()
            }
        }
        if (large) {
            Text(
                text = title,
                color = IosColors.Label,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, top = 4.dp, bottom = 8.dp)
            )
        }
    }
}

/** Rounded grouped container used across the built-in apps. */
@Composable
fun IosCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(IosColors.Card)
    ) { content() }
}

@Composable
fun IosRow(
    title: String,
    subtitle: String? = null,
    value: String? = null,
    leading: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leading != null) {
            leading()
            Spacer(Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = IosColors.Label, fontSize = 16.sp)
            if (subtitle != null) {
                Text(subtitle, color = IosColors.SecondaryLabel, fontSize = 13.sp)
            }
        }
        if (value != null) {
            Text(value, color = IosColors.SecondaryLabel, fontSize = 15.sp)
        }
        if (onClick != null) {
            Spacer(Modifier.width(6.dp))
            Text("\u203A", color = IosColors.TertiaryLabel, fontSize = 18.sp)
        }
    }
}

@Composable
fun IosSectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        color = IosColors.SecondaryLabel,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 20.dp, top = 18.dp, bottom = 6.dp)
    )
}

@Composable
fun IosEmptyState(text: String, hint: String? = null) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text, color = IosColors.SecondaryLabel, fontSize = 17.sp, fontWeight = FontWeight.Medium)
        if (hint != null) {
            Spacer(Modifier.height(6.dp))
            Text(hint, color = IosColors.TertiaryLabel, fontSize = 13.sp)
        }
    }
}

/** Small coloured rounded square used as a built-in app icon. */
@Composable
fun GlyphIcon(
    glyph: String,
    color: Color,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.225f))
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = glyph,
            color = Color.White,
            fontSize = (size.value * 0.46f).sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

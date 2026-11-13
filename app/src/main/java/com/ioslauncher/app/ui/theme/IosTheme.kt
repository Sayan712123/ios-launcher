package com.ioslauncher.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** iOS system palette (dark appearance). */
object IosColors {
    val Blue = Color(0xFF0A84FF)
    val Green = Color(0xFF30D158)
    val Red = Color(0xFFFF453A)
    val Orange = Color(0xFFFF9F0A)
    val Yellow = Color(0xFFFFD60A)
    val Purple = Color(0xFFBF5AF2)
    val Pink = Color(0xFFFF375F)
    val Teal = Color(0xFF64D2FF)
    val Indigo = Color(0xFF5E5CE6)
    val Gray = Color(0xFF8E8E93)

    val Label = Color(0xFFFFFFFF)
    val SecondaryLabel = Color(0x99EBEBF5)
    val TertiaryLabel = Color(0x4DEBEBF5)

    val Glass = Color(0x33FFFFFF)
    val GlassStrong = Color(0x4DFFFFFF)
    val GlassDark = Color(0x59000000)
    val Sheet = Color(0xF21C1C1E)
    val Card = Color(0x1FFFFFFF)
}

private val IosTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        letterSpacing = 0.37.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        letterSpacing = 0.06.sp
    )
)

private val IosColorScheme = darkColorScheme(
    primary = IosColors.Blue,
    onPrimary = Color.White,
    secondary = IosColors.Green,
    background = Color.Black,
    onBackground = IosColors.Label,
    surface = IosColors.Sheet,
    onSurface = IosColors.Label,
    error = IosColors.Red
)

val IosShapes = object {
    val icon = RoundedCornerShape(14.dp)
    val card = RoundedCornerShape(20.dp)
    val sheet = RoundedCornerShape(28.dp)
    val pill = RoundedCornerShape(50)
}

@Composable
fun IosLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = IosColorScheme,
        typography = IosTypography,
        content = content
    )
}

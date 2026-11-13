package com.ioslauncher.app.ui

import androidx.compose.ui.graphics.Color

/**
 * Apps that ship inside the launcher itself, so the phone has a complete
 * iOS-looking app set even before anything else is installed.
 */
data class BuiltinApp(
    val id: String,
    val name: String,
    val glyph: String,
    val color: Color
)

val builtinApps: List<BuiltinApp> = listOf(
    BuiltinApp("phone", "Phone", "\u2706", Color(0xFF34C759)),
    BuiltinApp("messages", "Messages", "\u2709", Color(0xFF30D158)),
    BuiltinApp("contacts", "Contacts", "\u263A", Color(0xFF8E8E93)),
    BuiltinApp("camera", "Camera", "\u25C9", Color(0xFF636366)),
    BuiltinApp("photos", "Photos", "\u2740", Color(0xFFFF9F0A)),
    BuiltinApp("calculator", "Calculator", "=", Color(0xFFFF9F0A)),
    BuiltinApp("notes", "Notes", "\u270E", Color(0xFFFFD60A)),
    BuiltinApp("clock", "Clock", "\u25F7", Color(0xFF1C1C1E)),
    BuiltinApp("calendar", "Calendar", "\u25A6", Color(0xFFFF3B30)),
    BuiltinApp("files", "Files", "\u25A4", Color(0xFF0A84FF)),
    BuiltinApp("music", "Music", "\u266A", Color(0xFFFF2D55)),
    BuiltinApp("weather", "Weather", "\u2600", Color(0xFF32ADE6)),
    BuiltinApp("reminders", "Reminders", "\u2611", Color(0xFFFF9500)),
    BuiltinApp("settings", "Settings", "\u2699", Color(0xFF8E8E93))
)

fun builtinById(id: String): BuiltinApp? = builtinApps.firstOrNull { it.id == id }

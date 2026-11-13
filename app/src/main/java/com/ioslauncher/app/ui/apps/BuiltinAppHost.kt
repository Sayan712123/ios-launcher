package com.ioslauncher.app.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ioslauncher.app.ui.LauncherViewModel
import com.ioslauncher.app.ui.components.IosStatusBar

/** Common frame for every built-in app: status bar + content. */
@Composable
fun BuiltinAppHost(
    id: String,
    vm: LauncherViewModel,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        IosStatusBar()
        Box(modifier = Modifier.fillMaxSize()) {
            when (id) {
                "calculator" -> CalculatorApp(onBack = onBack)
                "notes" -> NotesApp(onBack = onBack)
                "phone" -> PhoneApp(onBack = onBack)
                "messages" -> MessagesApp(onBack = onBack)
                "contacts" -> ContactsApp(onBack = onBack)
                "camera" -> CameraApp(onBack = onBack)
                "photos" -> PhotosApp(onBack = onBack)
                "clock" -> ClockApp(onBack = onBack)
                "calendar" -> CalendarApp(onBack = onBack)
                "files" -> FilesApp(onBack = onBack)
                "music" -> MusicApp(onBack = onBack)
                "weather" -> WeatherApp(onBack = onBack)
                "reminders" -> RemindersApp(onBack = onBack)
                "settings" -> SettingsApp(vm = vm, onBack = onBack)
                else -> Box(Modifier.fillMaxSize())
            }
        }
    }
}

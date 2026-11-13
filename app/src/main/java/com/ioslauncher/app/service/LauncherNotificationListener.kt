package com.ioslauncher.app.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Feeds the iOS-style notification centre. Only the fields the UI draws are
 * kept (no bitmaps, no full extras) so memory stays small.
 */
class LauncherNotificationListener : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        connected.value = true
        refresh()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        connected.value = false
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        refresh()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        refresh()
    }

    private fun refresh() {
        val items = try {
            activeNotifications
                ?.filter { it.isClearable && it.notification != null }
                ?.mapNotNull { toItem(it) }
                ?.sortedByDescending { it.postedAt }
                ?.take(40)
                ?: emptyList()
        } catch (t: Throwable) {
            emptyList()
        }
        _notifications.value = items
    }

    private fun toItem(sbn: StatusBarNotification): NotificationItem? {
        val n: Notification = sbn.notification ?: return null
        val extras = n.extras ?: return null
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_TITLE_BIG)?.toString()
            ?: return null
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
            ?: ""
        return NotificationItem(
            id = sbn.key ?: "${sbn.packageName}-${sbn.id}",
            packageName = sbn.packageName ?: "",
            title = title,
            text = text,
            postedAt = sbn.postTime
        )
    }

    companion object {
        private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
        val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

        private val connected = MutableStateFlow(false)
        val isConnected: StateFlow<Boolean> = connected.asStateFlow()

        fun clearAll() {
            _notifications.value = emptyList()
        }
    }
}

data class NotificationItem(
    val id: String,
    val packageName: String,
    val title: String,
    val text: String,
    val postedAt: Long
)

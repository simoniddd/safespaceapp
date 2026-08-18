package com.example.safespace.notification

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.safespace.core.domain.model.notification.BankNotificationParser
import com.example.safespace.core.domain.model.notification.NotificationPayload
import org.koin.android.ext.android.getKoin

class NotificationLogger : NotificationListenerService() {

    private val parsers: List<BankNotificationParser> by lazy {
        getKoin().getAll<BankNotificationParser>()
    }

    override fun onListenerConnected() {
        Log.d(TAG, "Listener connected")
    }

    override fun onListenerDisconnected() {
        Log.d(TAG, "Listener disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if ((sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0) return

        val extras = sbn.notification.extras
        val payload = NotificationPayload(
            packageName = sbn.packageName,
            title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString(),
            text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString(),
            bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString(),
            postTime = sbn.postTime
        )

        val parser = parsers.firstOrNull { it.canHandle(payload.packageName) } ?: return

        val expense = parser.parse(payload)

        Log.d(
            TAG, "🎉 Успешно словили расход: ${expense}"
        )
    }

    companion object {
        private const val TAG = "NL"
    }
}
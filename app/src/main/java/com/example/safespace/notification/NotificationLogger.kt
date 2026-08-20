package com.example.safespace.notification

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.safespace.core.domain.model.notification.BankNotificationParser
import com.example.safespace.core.domain.model.notification.NotificationPayload
import com.example.safespace.core.domain.model.notification.ParseResult
import com.example.safespace.core.domain.model.notification.ParsedTransaction
import com.example.safespace.core.domain.repository.PendingExpenseDraftRepository
import com.example.safespace.core.domain.repository.SaveResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject

class NotificationLogger : NotificationListenerService() {

    private val parsers: List<BankNotificationParser> by lazy {
        getKoin().getAll<BankNotificationParser>()
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val repo: PendingExpenseDraftRepository by inject()

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

        val result: ParseResult = parser.parse(payload)

        when (result) {
            is ParseResult.Success -> handleSuccess(result.tx)
            is ParseResult.NotATransaction -> {}
            is ParseResult.Unrecognized ->
                Log.w(TAG, "Unrecognized: reason=${result.reason}, pkg=${payload.packageName}")
        }
    }

    private fun handleSuccess(tx: ParsedTransaction) {
        serviceScope.launch {
            try {
                when (val save = repo.saveIfNew(tx)) {
                    is SaveResult.Saved -> Log.d(TAG, "saved draft=${save.draftId}")
                    SaveResult.Duplicate -> Log.d(TAG, "duplicate, skipped: $tx")
                }
            } catch (c: CancellationException) {
                throw c
            } catch (t: Throwable) {
                Log.e(TAG, "saveIfNew failed for $tx", t)
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "NL"
    }
}
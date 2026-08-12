package com.example.safespace.core.data.notification

import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.core.domain.model.notification.BankNotificationParser
import com.example.safespace.core.domain.model.notification.NotificationPayload
import java.util.UUID

class FreedomNotificationParser: BankNotificationParser {
    override fun canHandle(packageName: String): Boolean = packageName == "ffinbank.MyFreedom"

    override fun parse(payload: NotificationPayload): ExpensesItem? {
        val title = payload.title ?: return null
        val textToParse = payload.bigText ?: return null

        if(!title.startsWith("Покупка", ignoreCase = true)) return null
        if(!textToParse.startsWith("-")) return null

        val merchant = title.removePrefix("Покупка").trim()

        val amountLine = textToParse.split("\n").first()
        val amountStr = amountLine.replace(Regex("[^0-9]"), "")
        val amount = amountStr.toIntOrNull() ?: return null

        return ExpensesItem(
            id = UUID.randomUUID().toString(),
            amount = amount.toDouble(),
            description = merchant,
            category = "",
            dateString = "",
            timeStamp = payload.postTime,
            eventId = null,
            emotion = null,
            recurringEventId = null
        )
    }
}
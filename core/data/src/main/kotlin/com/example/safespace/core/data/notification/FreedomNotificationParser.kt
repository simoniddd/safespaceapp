package com.example.safespace.core.data.notification

import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.core.domain.model.notification.BankNotificationParser
import com.example.safespace.core.domain.model.notification.NotificationPayload
import java.util.UUID

class FreedomNotificationParser : BankNotificationParser {

    override fun canHandle(packageName: String): Boolean =
        packageName == FREEDOM_PACKAGE

    override fun parse(payload: NotificationPayload): ExpensesItem? {
        val title = payload.title ?: return null
        val text = payload.bigText ?: payload.text ?: return null

        if (!title.startsWith(PURCHASE_PREFIX)) return null

        val merchant = title.removePrefix(PURCHASE_PREFIX).trim()
        if (merchant.isEmpty()) return null

        val amountLine = text.lineSequence().firstOrNull() ?: return null
        if (!amountLine.startsWith("-")) return null

        val amount = parseAmount(amountLine) ?: return null

        return ExpensesItem(
            id = UUID.randomUUID().toString(),
            amount = amount,
            description = merchant,
            category = "",
            dateString = "",
            timeStamp = payload.postTime,
            eventId = null,
            emotion = null,
            recurringEventId = null,
        )
    }

    private fun parseAmount(amountLine: String): Double? {
        // KZ-формат "1 500,50 ₸": thousand separator = space или NBSP ( ), decimal = запятая
        val cleaned = amountLine
            .removePrefix("-")
            .replace("₸", "")
            .replace(" ", "")
            .replace(" ", "")
            .replace(",", ".")
            .trim()
        return cleaned.toDoubleOrNull()
    }

    private companion object {
        const val FREEDOM_PACKAGE = "ffinbank.MyFreedom"
        const val PURCHASE_PREFIX = "Покупка "
    }
}

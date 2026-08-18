package com.example.safespace.core.data.notification

import com.example.safespace.core.domain.model.notification.BankNotificationParser
import com.example.safespace.core.domain.model.notification.NotificationPayload
import com.example.safespace.core.domain.model.notification.ParseResult
import com.example.safespace.core.domain.model.notification.ParsedTransaction
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class FreedomNotificationParser : BankNotificationParser {

    override fun canHandle(packageName: String): Boolean =
        packageName == FREEDOM_PACKAGE

    override fun parse(payload: NotificationPayload): ParseResult {
        val title = payload.title ?: return ParseResult.Unrecognized("no title")

        if (!title.startsWith(PURCHASE_PREFIX)) {
            return ParseResult.NotATransaction
        }

        val text = payload.bigText ?: payload.text
        ?: return ParseResult.Unrecognized("no notification body")

        val merchant = title.removePrefix(PURCHASE_PREFIX).trim()
        if (merchant.isEmpty())
            return ParseResult.Unrecognized("empty merchant after 'Покупка ' prefix")

        val amountLine =
            text.lineSequence().firstOrNull() ?: return ParseResult.Unrecognized("empty body")

        if (!amountLine.startsWith("-")) return ParseResult.NotATransaction

        val parsed = parseAmount(amountLine)
            ?: return ParseResult.Unrecognized("cannot parse amount: $amountLine")

        return ParseResult.Success(
            ParsedTransaction(
                amount = parsed.amount,
                currency = parsed.currency,
                merchant = merchant,
                occurredAt = Instant.fromEpochMilliseconds(payload.postTime),
            )
        )
    }

    private fun parseAmount(amountLine: String): ParsedAmount? {
        val currency = when {
            amountLine.contains("₸") -> "KZT"
            amountLine.contains("$") -> "USD"
            else -> return null
        }
        val cleaned = amountLine
            .removePrefix("-")
            .replace("₸", "").replace("$", "")
            .replace(" ", "").replace("\u00A0", "")
            .replace(",", ".")
            .trim()
        val amount = cleaned.toDoubleOrNull() ?: return null
        return ParsedAmount(amount, currency)
    }

    private data class ParsedAmount(val amount: Double, val currency: String)
    private companion object {
        const val FREEDOM_PACKAGE = "ffinbank.MyFreedom"
        const val PURCHASE_PREFIX = "Покупка "
    }
}

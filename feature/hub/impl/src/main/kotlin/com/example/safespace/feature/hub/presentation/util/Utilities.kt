package com.example.safespace.feature.hub.presentation.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.NumberFormat
import java.util.Locale
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal fun formatAmount(amount: Double, currency: String): String {
    val symbol = when (currency) {
        "KZT" -> "₸"
        "USD" -> "$"
        "EUR" -> "€"
        "RUB" -> "₽"
        else -> currency
    }
    val body = NumberFormat.getIntegerInstance(Locale("ru", "RU")).format(amount.toLong())
    return if (symbol == "$" || symbol == "€") "$symbol$body" else "$body $symbol"
}

@OptIn(ExperimentalTime::class)
fun formatTime(epochMillis: Long): String {
    return Instant.fromEpochMilliseconds(epochMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault()).time
        .let { "%02d:%02d".format(it.hour, it.minute) }
}

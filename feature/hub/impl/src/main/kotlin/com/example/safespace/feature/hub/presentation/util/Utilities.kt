package com.example.safespace.feature.hub.presentation.util

import java.text.NumberFormat
import java.util.Locale

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

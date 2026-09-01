package com.example.safespace.feature.hub.presentation.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Clock
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

// Форматирование шагов с неразрывным пробелом-разделителем тысяч: "8 042".
// Не String.format(%,d) — чтобы не тащить локаль-зависимость (в ru будет ", ").
fun formatSteps(count: Int): String =
    buildString {
        val str = count.toString()
        str.forEachIndexed { i, ch ->
            if (i > 0 && (str.length - i) % 3 == 0) append(' ')
            append(ch)
        }
        append(" шагов")
    }

@OptIn(ExperimentalTime::class)
fun startOfTodayMillis(): Long =
    Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date
        .atStartOfDayIn(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()
@OptIn(ExperimentalTime::class)
fun formatDateHeader(): String {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val javaDate = today.toJavaLocalDate()
// Форматирует дату в "ПЯТНИЦА · 4 ИЮЛЯ"
    val datePattern = DateTimeFormatter.ofPattern("EEEE · d MMMM", Locale("ru"))
    val formattedHeaderDate = javaDate.format(datePattern).uppercase()
    return formattedHeaderDate
}
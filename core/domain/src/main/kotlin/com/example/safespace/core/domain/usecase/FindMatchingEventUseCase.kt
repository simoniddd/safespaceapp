package com.example.safespace.core.domain.usecase

import com.example.safespace.core.domain.model.CalendarEventItem
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

class FindMatchingEventUseCase {

    // Асимметричный буфер: только ПОСЛЕ события. Расход до встречи ≠ про встречу;
    // платежи (счёт, чек) технически происходят к концу активности.
    @OptIn(ExperimentalTime::class)
    operator fun invoke(
        expenseTime: Long,
        events: List<CalendarEventItem>,
        bufferAfter: Duration = 1.hours,
    ): List<CalendarEventItem> =
        events
            .filter { event -> expenseTime in event.timeStamp until event.windowEndMs(bufferAfter) }
            .sortedWith(compareBy({ it.timeStamp }, { it.id }))
}

/**
 * Верхняя (exclusive) граница окна матча события: конец события + асимметричный буфер.
 * Используется в паре с `until`: окно = `[timeStamp, windowEndMs)`.
 */
@OptIn(ExperimentalTime::class)
private fun CalendarEventItem.windowEndMs(bufferAfter: Duration): Long =
    timeStamp + durationMin.minutes.inWholeMilliseconds + bufferAfter.inWholeMilliseconds

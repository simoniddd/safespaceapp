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
    ): List<CalendarEventItem> {
        val bufferMs = bufferAfter.inWholeMilliseconds
        return events
            .filter { event ->
                val startMs = event.timeStamp
                val endMs = startMs + event.durationMin.minutes.inWholeMilliseconds
                // окно [startMs, endMs + bufferMs) — полуоткрытое, «until» = exclusive top
                expenseTime in startMs until (endMs + bufferMs)
            }
            .sortedWith(compareBy({ it.timeStamp }, { it.id }))
    }
}
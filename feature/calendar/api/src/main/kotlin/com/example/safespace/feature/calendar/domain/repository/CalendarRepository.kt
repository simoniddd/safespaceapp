package com.example.safespace.feature.calendar.domain.repository

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.EventType
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface CalendarRepository {
    // для реактивного обновления UI
    fun getEventsForDate(date: LocalDate): Flow<List<CalendarEventItem>>

    suspend fun addEvent(
        title: String,
        timeStamp: Long,
        dateString: String,
        timeString: String,
        durationMin: Int,
        type: EventType
    )
}
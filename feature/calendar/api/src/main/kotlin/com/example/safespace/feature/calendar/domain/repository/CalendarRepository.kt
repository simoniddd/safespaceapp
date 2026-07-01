package com.example.safespace.feature.calendar.domain.repository

import com.example.safespace.feature.calendar.domain.model.CalendarEventItem
import com.example.safespace.feature.calendar.domain.model.EventType
import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    // для реактивного обновления UI
    fun getEventsForDate(dateString: String): Flow<List<CalendarEventItem>>

    suspend fun addEvent(
        title: String,
        timeStamp: Long,
        dateString: String,
        timeString: String,
        durationMin: Int,
        type: EventType
    )
}
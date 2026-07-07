package com.example.safespace.feature.hub.fake

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.EventType
import com.example.safespace.feature.calendar.domain.repository.CalendarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

class FakeCalendarRepository : CalendarRepository {

    private val _events = MutableStateFlow<List<CalendarEventItem>>(emptyList())

    fun emit(events: List<CalendarEventItem>) {
        _events.value = events
    }

    override fun getEventsForDate(date: LocalDate): Flow<List<CalendarEventItem>> = _events

    override suspend fun addEvent(
        title: String,
        timeStamp: Long,
        dateString: String,
        timeString: String,
        durationMin: Int,
        type: EventType
    ) = Unit
}
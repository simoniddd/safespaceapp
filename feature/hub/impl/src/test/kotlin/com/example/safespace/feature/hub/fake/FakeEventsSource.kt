package com.example.safespace.feature.hub.fake

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.source.EventsSource
import java.time.LocalDate

class FakeEventsSource : EventsSource {

    private var events: List<CalendarEventItem> = emptyList()

    fun setEvents(events: List<CalendarEventItem>) {
        this.events = events
    }

    override suspend fun getEventsForDate(date: LocalDate): List<CalendarEventItem> = events
}

package com.example.safespace.core.domain.source

import com.example.safespace.core.domain.model.CalendarEventItem
import java.time.LocalDate

interface EventsSource {
    suspend fun getEventsForDate(date: LocalDate): List<CalendarEventItem>
}
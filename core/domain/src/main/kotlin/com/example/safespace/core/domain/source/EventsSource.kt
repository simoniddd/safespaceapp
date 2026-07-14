package com.example.safespace.core.domain.source

import com.example.safespace.core.domain.model.CalendarEventItem
import kotlinx.datetime.LocalDate

interface EventsSource {
    suspend fun getEventsForDate(date: LocalDate): List<CalendarEventItem>
}
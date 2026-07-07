package com.example.safespace.feature.calendar.domain.source

import kotlinx.coroutines.flow.Flow

interface CalendarSource {
    val enabledCalendarIds: Flow<Set<Long>>
    suspend fun setEnabledCalendarIds(ids: Set<Long>)
}
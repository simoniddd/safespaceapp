package com.example.safespace.feature.calendar.domain.source

import kotlinx.coroutines.flow.Flow

interface CalendarSource {
    val hiddenCalendarIds: Flow<Set<Long>>
    suspend fun setHiddenCalendarIds(ids: Set<Long>)
}
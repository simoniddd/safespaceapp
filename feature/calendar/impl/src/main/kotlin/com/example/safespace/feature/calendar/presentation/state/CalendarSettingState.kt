package com.example.safespace.feature.calendar.presentation.state

import com.example.safespace.core.domain.model.CalendarInfo

data class CalendarSettingState(
    val calendarList: List<CalendarInfo> = emptyList(),
    val enabledCalendarIds: Set<Long> = emptySet()
)
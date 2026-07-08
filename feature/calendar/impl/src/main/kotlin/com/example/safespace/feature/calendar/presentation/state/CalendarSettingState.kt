package com.example.safespace.feature.calendar.presentation.state

import androidx.compose.ui.graphics.Color
import com.example.safespace.core.domain.model.CalendarInfo

data class CalendarSettingState(
    val calendarList: List<CalendarInfo> = emptyList(),
    val hiddenCalendarIds: Set<Long> = emptySet(),
    val isLoaded: Boolean = false
) {
    fun toUiState(): CalendarSettingUiState {
        if (!isLoaded) return CalendarSettingUiState.Loading
        if (calendarList.isEmpty()) return CalendarSettingUiState.Empty
        val calendars = calendarList.map { info ->
            CalendarUi(
                id = info.id,
                name = info.name,
                color = Color(info.color),
                isVisible = info.id !in hiddenCalendarIds
            )
        }
        return CalendarSettingUiState.Content(
            sections = listOf(CalendarSection("Мои Календари", calendars))
        )
    }
}
package com.example.safespace.feature.calendar.presentation.action

sealed interface CalendarSettingAction {
    data class OnCalendarToggled(val calendarId: Long) : CalendarSettingAction
    data object OnBackClick : CalendarSettingAction
}
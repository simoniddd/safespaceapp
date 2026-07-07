package com.example.safespace.feature.calendar.presentation.viwemodel

import androidx.lifecycle.viewModelScope
import com.example.safespace.core.presentation.StatefulViewModel
import com.example.safespace.core.presentation.navigation.Navigator
import com.example.safespace.feature.calendar.data.source.SystemCalendarsSource
import com.example.safespace.feature.calendar.domain.source.CalendarSource
import com.example.safespace.feature.calendar.presentation.action.CalendarSettingAction
import com.example.safespace.feature.calendar.presentation.state.CalendarSettingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalendarSettingViewmodel(
    private val calendars: SystemCalendarsSource,
    private val calendarIds: CalendarSource,
    navigator: Navigator
) : StatefulViewModel<CalendarSettingState>(
    initial = CalendarSettingState(),
    navigator = navigator
) {
    init {
        loadCalendars()
        viewModelScope.launch {
            calendarIds.enabledCalendarIds.collect { ids ->
                updateState { copy(enabledCalendarIds = ids) }
            }
        }
    }

    fun onAction(action: CalendarSettingAction) {
        when (action) {
            CalendarSettingAction.OnBackClick -> navigateUp()
            is CalendarSettingAction.OnCalendarToggled -> addCalendarId(action.calendarId)
        }
    }

    fun loadCalendars() {
        viewModelScope.launch { getCalendars() }
    }

    private suspend fun getCalendars() = withContext(Dispatchers.IO) {
        val calendarsUpdated = calendars.getCalendars()
        updateState {
            copy(
                calendarList = calendarsUpdated
            )
        }
    }

    private fun addCalendarId(selectedId: Long) {
        val currentIds = state.value.enabledCalendarIds
        val newIds =
            if (selectedId in currentIds) currentIds - selectedId else currentIds + selectedId
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                calendarIds.setEnabledCalendarIds(newIds)
            }
        }
    }
}
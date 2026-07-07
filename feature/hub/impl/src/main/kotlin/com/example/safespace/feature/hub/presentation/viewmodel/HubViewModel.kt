package com.example.safespace.feature.hub.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.source.EventsSource
import com.example.safespace.core.presentation.StatefulViewModel
import com.example.safespace.core.presentation.navigation.Navigator
import com.example.safespace.feature.calendar.domain.source.CalendarSource
import com.example.safespace.feature.calendar.navigation.CalendarEntryPoint
import com.example.safespace.feature.expenses.domain.repository.ExpensesRepository
import com.example.safespace.feature.expenses.navigation.ExpensesEntryPoint
import com.example.safespace.feature.hub.domain.interactor.BuildTimelineUseCase
import com.example.safespace.feature.hub.presentation.state.HubState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class HubViewModel(
    private val expensesRepository: ExpensesRepository,
    private val expensesEntryPoint: ExpensesEntryPoint,
    private val calendarEntryPoint: CalendarEntryPoint,
    private val eventsSource: EventsSource,
    private val calendarSource: CalendarSource,
    navigator: Navigator
) : StatefulViewModel<HubState>(HubState(), navigator) {

    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    private val buildTimeLine = BuildTimelineUseCase()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val systemEventsFlow: Flow<List<CalendarEventItem>> =
        _refreshTrigger.flatMapLatest {
            flow {
                emit(
                    eventsSource.getEventsForDate(LocalDate.now())
                )
            }
        }

    val uiState: StateFlow<HubState> = combine(
        expensesRepository.getAllExpenses(),
        systemEventsFlow,
        calendarSource.enabledCalendarIds
    ) { expenses, events, calendarIds ->
        val filteredEvents = if (calendarIds.isEmpty()) events
        else events.filter { it.calendarId == 0L || it.calendarId in calendarIds }
        HubState(
            isLoading = false,
            dailyExpenses = expenses.sumOf { it.amount },
            timelineItems = buildTimeLine(expenses, filteredEvents)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HubState(isLoading = true)
    )

    init {
        refresh()
    }

    fun refresh() {
        _refreshTrigger.tryEmit(Unit)
    }

    fun onExpensesClicked() {
        navigateTo(expensesEntryPoint.expensesRoute())
    }

    fun onCalendarClicked() {
        navigateTo(calendarEntryPoint.calendarRoute())
    }
}

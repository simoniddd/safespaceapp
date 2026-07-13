package com.example.safespace.feature.hub.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.repository.IntentionRepository
import com.example.safespace.core.domain.source.EventsSource
import com.example.safespace.core.domain.state.IntentionState
import com.example.safespace.core.domain.worker.IntentionScheduler
import com.example.safespace.core.presentation.StatefulViewModel
import com.example.safespace.core.presentation.navigation.Navigator
import com.example.safespace.feature.calendar.domain.source.CalendarSource
import com.example.safespace.feature.calendar.navigation.CalendarEntryPoint
import com.example.safespace.feature.expenses.domain.repository.ExpensesRepository
import com.example.safespace.feature.expenses.navigation.ExpensesEntryPoint
import com.example.safespace.feature.hub.domain.interactor.BuildTimelineUseCase
import com.example.safespace.feature.hub.presentation.state.HubState
import com.example.safespace.feature.hub.presentation.state.PendingQuestion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HubViewModel(
    private val expensesRepository: ExpensesRepository,
    private val expensesEntryPoint: ExpensesEntryPoint,
    private val calendarEntryPoint: CalendarEntryPoint,
    private val eventsSource: EventsSource,
    private val calendarSource: CalendarSource,
    private val intentionRepository: IntentionRepository,
    private val intentionScheduler: IntentionScheduler,
    navigator: Navigator,
) : StatefulViewModel<HubState>(HubState(), navigator) {

    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val buildTimeLine = BuildTimelineUseCase()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val systemEventsFlow: Flow<List<CalendarEventItem>> =
        _refreshTrigger.flatMapLatest {
            flow { emit(eventsSource.getEventsForDate(LocalDate.now())) }
        }

    val uiState: StateFlow<HubState> = combine(
        expensesRepository.getAllExpenses(),
        systemEventsFlow,
        calendarSource.hiddenCalendarIds,
        intentionRepository.observeIntentions(),
        intentionRepository.observeDoneCountByEventId(),
    ) { expenses, events, hiddenIds, intentions, doneCounts ->
        val filteredEvents = if (hiddenIds.isEmpty()) events
        else events.filter { it.calendarId !in hiddenIds }

        val eventsWithState = filteredEvents.map { event ->
            event.copy(intentionState = intentions[event.id])
        }

        val pendingEvent = eventsWithState.firstOrNull { it.intentionState == IntentionState.PENDING }

        HubState(
            isLoading = false,
            dailyExpenses = expenses.sumOf { it.amount },
            timelineItems = buildTimeLine(expenses, eventsWithState),
            pendingQuestion = pendingEvent?.let {
                PendingQuestion.IntentionCheck(
                    eventId = it.id,
                    instanceStart = it.timeStamp,
                    eventTitle = it.title,
                )
            },
            doneCountByEventId = doneCounts,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HubState(isLoading = true),
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

    fun markEventAsIntention(eventId: String, instanceStart: Long, durationMin: Int) {
        viewModelScope.launch {
            intentionRepository.markEventAsIntention(eventId)
            val endTimeMs = instanceStart + durationMin * 60_000L
            val delayMs = (endTimeMs - System.currentTimeMillis()).coerceAtLeast(0L)
            intentionScheduler.scheduleIntentionCheck(eventId, instanceStart, delayMs)
        }
    }

    fun confirmIntention(eventId: String, instanceStart: Long) {
        viewModelScope.launch {
            intentionRepository.updateStatus(eventId, instanceStart, IntentionState.DONE)
        }
    }

    fun skipIntention(eventId: String, instanceStart: Long) {
        viewModelScope.launch {
            intentionRepository.updateStatus(eventId, instanceStart, IntentionState.SKIPPED)
        }
    }

    fun postponeIntention(eventId: String, instanceStart: Long) {
        viewModelScope.launch {
            // сдвигаем проверку на час вперёд и скрываем карточку
            intentionScheduler.scheduleIntentionCheck(
                eventId = eventId,
                instanceStart = instanceStart,
                delayMillis = 60 * 60_000L,
            )
            intentionRepository.updateStatus(eventId, instanceStart, IntentionState.UPCOMING)
        }
    }
}

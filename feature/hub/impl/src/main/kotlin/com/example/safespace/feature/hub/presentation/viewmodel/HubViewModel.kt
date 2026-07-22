package com.example.safespace.feature.hub.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.WeatherIcon
import com.example.safespace.core.domain.model.health.DailySteps
import com.example.safespace.core.domain.model.health.HealthDataType
import com.example.safespace.core.domain.model.health.HealthState
import com.example.safespace.core.domain.repository.HealthRepository
import com.example.safespace.core.domain.repository.IntentionRepository
import com.example.safespace.core.domain.repository.WeatherRepository
import com.example.safespace.core.domain.source.EventsSource
import com.example.safespace.core.domain.state.IntentionState
import com.example.safespace.core.domain.usecase.AutoConfirmIntentionsUseCase
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class HubViewModel(
    private val expensesRepository: ExpensesRepository,
    private val expensesEntryPoint: ExpensesEntryPoint,
    private val calendarEntryPoint: CalendarEntryPoint,
    private val eventsSource: EventsSource,
    private val calendarSource: CalendarSource,
    private val intentionRepository: IntentionRepository,
    private val intentionScheduler: IntentionScheduler,
    private val weatherRepository: WeatherRepository,
    private val healthRepository: HealthRepository,
    private val autoConfirmIntentionsUseCase: AutoConfirmIntentionsUseCase,
    navigator: Navigator,
) : StatefulViewModel<HubState>(HubState(), navigator) {

    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val buildTimeLine = BuildTimelineUseCase()

    // Атомарный набор: у UI один launcher, просит сразу три типа.
    // Модель уже поддерживает per-type грануляцию (NoPermission.toRequest),
    // так что переход на «частичное присутствие» не потребует ломки VM.
    val healthPermissions: Set<String> =
        healthRepository.permissionsFor(HealthDataType.entries.toSet())

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
    private val systemEventsFlow: Flow<List<CalendarEventItem>> =
        _refreshTrigger.flatMapLatest {
            flow { emit(eventsSource.getEventsForDate(Clock.System.todayIn(TimeZone.currentSystemDefault()))) }
        }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
    private val healthFlow: Flow<HealthState> = _refreshTrigger.flatMapLatest {
        flow {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            emit(healthRepository.getState(today))
        }
    }

    private val expensesWithWeather = combine(
        expensesRepository.getAllExpenses(),
        weatherRepository.observeToday(),
    ) { expenses, weather -> expenses to weather }

    private val intentionsFlow = combine(
        intentionRepository.observeIntentions(),
        intentionRepository.observeDoneCountByEventId(),
    ) { intentions, doneCounts -> intentions to doneCounts }

    @OptIn(ExperimentalTime::class)
    val uiState: StateFlow<HubState> = combine(
        expensesWithWeather,
        systemEventsFlow,
        calendarSource.hiddenCalendarIds,
        intentionsFlow,
        healthFlow,
    ) { expenses, events, hiddenIds, intentionsPair, healthState ->
        val (intentions, doneCounts) = intentionsPair

        val filteredEvents = if (hiddenIds.isEmpty()) events
        else events.filter { it.calendarId !in hiddenIds }

        val eventsWithState = filteredEvents.map { event ->
            event.copy(intentionState = intentions[event.id])
        }

        val available = (healthState as? HealthState.Available)?.data
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val steps = available?.steps?.let { DailySteps(count = it, date = today) }
        val sleep = available?.sleep
        val exercises = available?.exercises.orEmpty()

        val pendingEvent =
            eventsWithState.firstOrNull { it.intentionState == IntentionState.PENDING }

        HubState(
            isLoading = false,
            dailyExpenses = expenses.first.sumOf { it.amount },
            timelineItems = buildTimeLine(expenses.first, eventsWithState, steps, sleep, exercises),
            pendingQuestion = pendingEvent?.let {
                PendingQuestion.IntentionCheck(
                    eventId = it.id,
                    instanceStart = it.timeStamp,
                    eventTitle = it.title,
                )
            },
            doneCountByEventId = doneCounts,
            todayIcon = expenses.second,
            healthState = healthState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HubState(isLoading = true),
    )

    init {
        refresh()
        observeAutoConfirmations()
    }

    // Автоподтверждение намерения при ExerciseSession в окне события.
    // Отдельный collector, а НЕ внутри combine uiState: combine — про построение
    // презентационного состояния (чистая трансформация), а updateStatus — сайд-эффект
    // (пишет в БД). Смешивать нельзя: (1) combine потерял бы чистоту, (2) uiState стал бы
    // eager при подписке из VM, ломая тесты с ленивым setEvents().
    // Триггер — тот же _refreshTrigger, что кормит uiState. Двойное чтение Health/Events
    // за refresh — плата за развязку. Достаточно: реальный refresh случается на
    // ON_RESUME + вручную; между ними Health-данные всё равно не меняются.
    private fun observeAutoConfirmations() {
        viewModelScope.launch {
            _refreshTrigger.collect { runAutoConfirm() }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun runAutoConfirm() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val available = (healthRepository.getState(today) as? HealthState.Available)?.data ?: return
        if (available.exercises.isEmpty()) return

        val events = eventsSource.getEventsForDate(today)
        val intentions = intentionRepository.observeIntentions().first()
        val eventsWithState = events.map { it.copy(intentionState = intentions[it.id]) }

        autoConfirmIntentionsUseCase(
            events = eventsWithState,
            exercises = available.exercises,
            timeZone = TimeZone.currentSystemDefault(),
        ).forEach { c ->
            intentionRepository.updateStatus(c.eventId, c.instanceStart, IntentionState.DONE)
        }
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
            intentionScheduler.scheduleIntentionCheck(
                eventId = eventId,
                instanceStart = instanceStart,
                delayMillis = 60 * 60_000L,
            )
            intentionRepository.updateStatus(eventId, instanceStart, IntentionState.UPCOMING)
        }
    }

    @OptIn(ExperimentalTime::class)
    fun saveWeather(icon: WeatherIcon) {
        viewModelScope.launch {
            weatherRepository.saveWeather(Clock.System.todayIn(TimeZone.currentSystemDefault()), icon)
        }
    }
}

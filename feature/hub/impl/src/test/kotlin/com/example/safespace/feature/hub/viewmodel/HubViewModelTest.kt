@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)

package com.example.safespace.feature.hub.viewmodel

import app.cash.turbine.test
import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.EventType
import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.core.domain.usecase.AutoConfirmIntentionsUseCase
import com.example.safespace.feature.calendar.navigation.CalendarEntryPoint
import com.example.safespace.feature.expenses.navigation.ExpensesEntryPoint
import com.example.safespace.feature.hub.fake.FakeCalendarSource
import com.example.safespace.feature.hub.fake.FakeEventsSource
import com.example.safespace.feature.hub.fake.FakeExpensesRepository
import com.example.safespace.feature.hub.fake.FakeHealthRepository
import com.example.safespace.feature.hub.fake.FakeIntentionRepository
import com.example.safespace.feature.hub.fake.FakeIntentionScheduler
import com.example.safespace.feature.hub.fake.FakeNavigator
import com.example.safespace.feature.hub.fake.FakePendingExpenseDraftRepository
import com.example.safespace.feature.hub.fake.FakeWeatherRepository
import com.example.safespace.feature.hub.presentation.model.TimelineItem
import com.example.safespace.feature.hub.presentation.viewmodel.HubViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayIn
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class HubViewModelTest {

    private val fakeExpenseRepo = FakeExpensesRepository()
    private val fakeEventsSource = FakeEventsSource()
    private val fakeCalendarSource = FakeCalendarSource()
    private val testDispatcher = UnconfinedTestDispatcher()

    private val tz = TimeZone.currentSystemDefault()
    private val today = Clock.System.todayIn(tz)
    private val todayStartMillis = today.atStartOfDayIn(tz).toEpochMilliseconds()

    private val fakeExpensesEntryPoint = object : ExpensesEntryPoint {
        override fun expensesRoute(): Any = "expenses"
    }

    private val fakeCalendarEntryPoint = object : CalendarEntryPoint {
        override fun calendarRoute(): Any = "events"
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = HubViewModel(
        expensesRepository = fakeExpenseRepo,
        expensesEntryPoint = fakeExpensesEntryPoint,
        calendarEntryPoint = fakeCalendarEntryPoint,
        eventsSource = fakeEventsSource,
        calendarSource = fakeCalendarSource,
        intentionRepository = FakeIntentionRepository(),
        intentionScheduler = FakeIntentionScheduler(),
        weatherRepository = FakeWeatherRepository(),
        healthRepository = FakeHealthRepository(),
        pendingExpenseDraftRepository = FakePendingExpenseDraftRepository(),
        autoConfirmIntentionsUseCase = AutoConfirmIntentionsUseCase(),
        navigator = FakeNavigator(),
    )

    @Test
    fun `начальный стейт — пустые репозитории → две секции без элементов`() = runTest {
        val vm = createViewModel()

        vm.uiState.test {
            val state = awaitItem()

            assertEquals(false, state.isLoading)
            // multi-day: today + tomorrow всегда две секции, независимо от наличия данных
            assertEquals(2, state.days.size)
            assertEquals(emptyList<TimelineItem>(), state.days.flatMap { it.items })
        }
    }

    @Test
    fun `трата попадает только в секцию своего дня`() = runTest {
        val vm = createViewModel()
        val todayExpense = ExpensesItem(
            id = "1",
            amount = 150.0,
            description = "кофе",
            category = "food",
            dateString = "",
            timeStamp = todayStartMillis + 12 * 3600 * 1000, // сегодня в полдень
        )

        vm.uiState.test {
            awaitItem() // начальное после combine
            fakeExpenseRepo.emit(listOf(todayExpense))
            val updated = awaitItem()

            val todaySection = updated.days.first { it.date == today }
            val tomorrowSection = updated.days.first { it.date != today }

            assertEquals(1, todaySection.items.size)
            assertEquals(0, tomorrowSection.items.filterIsInstance<TimelineItem.Expense>().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `траты и события в today отсортированы по времени`() = runTest {
        val vm = createViewModel()
        val expenseMorning = ExpensesItem(
            id = "e1",
            amount = 100.0,
            description = "завтрак",
            category = "food",
            dateString = "",
            timeStamp = todayStartMillis + 8 * 3600 * 1000, // 08:00
        )
        val expenseEvening = ExpensesItem(
            id = "e2",
            amount = 200.0,
            description = "ужин",
            category = "food",
            dateString = "",
            timeStamp = todayStartMillis + 20 * 3600 * 1000, // 20:00
        )
        val eventNoon = CalendarEventItem(
            id = "ev1",
            title = "созвон",
            timeStamp = todayStartMillis + 12 * 3600 * 1000, // 12:00
            dateString = "",
            timeString = "12:00",
            calendarId = 0L,
            durationMin = 60,
            type = EventType.WORK,
        )
        val eventAfternoon = CalendarEventItem(
            id = "ev2",
            title = "спорт",
            timeStamp = todayStartMillis + 18 * 3600 * 1000, // 18:00
            dateString = "",
            timeString = "18:00",
            calendarId = 0L,
            durationMin = 90,
            type = EventType.ENTERTAINMENT,
        )

        fakeEventsSource.setEvents(listOf(eventNoon, eventAfternoon))

        vm.uiState.test {
            awaitItem()
            fakeExpenseRepo.emit(listOf(expenseMorning, expenseEvening))
            val updated = awaitItem()

            val todaySection = updated.days.first { it.date == today }
            val sortKeys = todaySection.items.map { it.sortKey }
            assertEquals(sortKeys.sorted(), sortKeys)
            assertEquals(4, todaySection.items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

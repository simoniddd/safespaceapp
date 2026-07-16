@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.safespace.feature.hub.viewmodel

import app.cash.turbine.test
import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.EventType
import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.feature.calendar.navigation.CalendarEntryPoint
import com.example.safespace.feature.expenses.navigation.ExpensesEntryPoint
import com.example.safespace.feature.hub.fake.FakeCalendarSource
import com.example.safespace.feature.hub.fake.FakeEventsSource
import com.example.safespace.feature.hub.fake.FakeExpensesRepository
import com.example.safespace.feature.hub.fake.FakeIntentionRepository
import com.example.safespace.feature.hub.fake.FakeHealthRepository
import com.example.safespace.feature.hub.fake.FakeIntentionScheduler
import com.example.safespace.feature.hub.fake.FakeNavigator
import com.example.safespace.feature.hub.fake.FakeWeatherRepository
import com.example.safespace.feature.hub.presentation.state.HubTimelineItem
import com.example.safespace.feature.hub.presentation.viewmodel.HubViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class HubViewModelTest {

    private val fakeExpenseRepo = FakeExpensesRepository()
    private val fakeEventsSource = FakeEventsSource()
    private val fakeCalendarSource = FakeCalendarSource()
    private val testDispatcher = UnconfinedTestDispatcher()

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
        navigator = FakeNavigator(),
    )

    @Test
    fun `начальный стейт — пустые репозитории → пустой таймлайн`() = runTest {
        val vm = createViewModel()

        vm.uiState.test {
            val state = awaitItem()

            assertEquals(false, state.isLoading)
            assertEquals(0.0, state.dailyExpenses)
            assertEquals(emptyList<HubTimelineItem>(), state.timelineItems)
        }
    }

    @Test
    fun `репозиторий выдает расходы и стейт обновляется`() = runTest {
        val vm = createViewModel()
        val someExpense = ExpensesItem(
            "",
            amount = 150.0,
            description = "TODO()",
            category = "TODO()",
            dateString = "TODO()",
            eventId = "TODO()"
        )

        vm.uiState.test {
            awaitItem()
            fakeExpenseRepo.emit(listOf(someExpense))
            val updatedState = awaitItem()

            assertEquals(150.0, updatedState.dailyExpenses)
            assertEquals(1, updatedState.timelineItems.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `оба источника выдают данные — расходы и события смешаны и отсортированы`() = runTest {
        val vm = createViewModel()
        val someExpense1 = ExpensesItem(
            "1",
            amount = 150.0,
            description = "TODO()",
            category = "TODO()",
            dateString = "TODO()",
            eventId = "TODO()"
        )
        val someExpense2 = ExpensesItem(
            "3",
            amount = 170.0,
            description = "TODO()",
            category = "TODO()",
            dateString = "TODO()",
            eventId = "TODO()"
        )
        val someEventA = CalendarEventItem(
            id = "2",
            title = "",
            timeStamp = 124125125,
            dateString = "TODO()",
            timeString = "TODO()",
            calendarId = 0L,
            durationMin = 90,
            type = EventType.ENTERTAINMENT,
        )
        val someEventB = CalendarEventItem(
            id = "4",
            title = "",
            timeStamp = 124125125,
            dateString = "TODO()",
            timeString = "TODO()",
            calendarId = 0L,
            durationMin = 100,
            type = EventType.ENTERTAINMENT,
        )

        fakeEventsSource.setEvents(listOf(someEventA, someEventB))

        vm.uiState.test {
            awaitItem() // пустое начальное состояние после refresh()
            fakeExpenseRepo.emit(listOf(someExpense1, someExpense2))
            val updatedState = awaitItem()

            assertEquals(320.0, updatedState.dailyExpenses)
            assertEquals(4, updatedState.timelineItems.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

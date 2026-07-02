@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.safespace.feature.hub.viewmodel

import app.cash.turbine.test
import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.EventType
import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.feature.calendar.navigation.CalendarEntryPoint
import com.example.safespace.feature.expenses.navigation.ExpensesEntryPoint
import com.example.safespace.feature.hub.fake.FakeCalendarRepository
import com.example.safespace.feature.hub.fake.FakeExpensesRepository
import com.example.safespace.feature.hub.fake.FakeNavigator
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
    private val fakeCalendarRepo = FakeCalendarRepository()
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
        calendarRepository = fakeCalendarRepo,
        expensesEntryPoint = fakeExpensesEntryPoint,
        calendarEntryPoint = fakeCalendarEntryPoint,
        navigator = FakeNavigator()
    )

    // Когда оба репозитория пустые → timelineItems пуст, dailyExpenses = 0.0, isLoading = false

    @Test
    fun `начальный стейт — пустые репозитории → пустой таймлайн`() = runTest {
        val vm = createViewModel()

        vm.state.test {
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

        vm.state.test {
            awaitItem()
            fakeExpenseRepo.emit(listOf(someExpense))
            val updatedState = awaitItem()

            assertEquals(150.0, updatedState.dailyExpenses)
            assertEquals(1, updatedState.timelineItems.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    /*
    оба репозитория выдают данные, и расходы и события смешаны и отсортированы.
    По аналогии с тестом 3 в BuildTimelineUseCaseTest — но теперь через ViewModel целиком.
     */
    @Test
    fun `оба репозитория выдают данные, и расходы и события смешаны и отсортированы`() = runTest {
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
            "2",
            title = "",
            timeStamp = 124125125,
            dateString = "TODO()",
            timeString = "TODO()",
            durationMin = 90,
            type = EventType.ENTERTAINMENT,
        )
        val someEventB = CalendarEventItem(
            "4",
            title = "",
            timeStamp = 124125125,
            dateString = "TODO()",
            timeString = "TODO()",
            durationMin = 100,
            type = EventType.ENTERTAINMENT,
        )

        val expenses = listOf(someExpense1, someExpense2)
        val events = listOf(someEventA, someEventB)

        vm.state.test {
            awaitItem()
            fakeExpenseRepo.emit(expenses)
            awaitItem()
            fakeCalendarRepo.emit(events)
            val updatedState = awaitItem()

            assertEquals(320.0, updatedState.dailyExpenses)
            assertEquals(4, updatedState.timelineItems.size)
        }
    }
}
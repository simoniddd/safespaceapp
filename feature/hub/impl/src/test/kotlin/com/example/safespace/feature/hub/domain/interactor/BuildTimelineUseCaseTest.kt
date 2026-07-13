package com.example.safespace.feature.hub.domain.interactor

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.EventType
import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.feature.hub.presentation.state.HubTimelineItem
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.util.Random

class BuildTimelineUseCaseTest {

    @Test
    fun `пустые списки возвращают пустой результат`() {
        val useCase = BuildTimelineUseCase()
        val expenses = emptyList<ExpensesItem>()
        val events = emptyList<CalendarEventItem>()

        val result = useCase(expenses, events)

        assertEquals(emptyList<HubTimelineItem>(), result)
    }

    @Test
    fun `разные расходы с разным временем должны вернуться отсортированными`() {
        val useCase = BuildTimelineUseCase()
        val events = emptyList<CalendarEventItem>()
        val expenses = listOf<ExpensesItem>(
            ExpensesItem(
                id = "1",
                amount = 100.0,
                description = "",
                category = "",
                dateString = "20:00"
            ),
            ExpensesItem(
                id = "2",
                amount = 50.0,
                description = "",
                category = "",
                dateString = "10:00"
            )
        )
        val result = useCase(expenses, events)
        val expected = listOf(
            HubTimelineItem.Expense(expenses[1]),  // "10:00" — первый
            HubTimelineItem.Expense(expenses[0])   // "20:00" — второй
        )
        assertEquals(expected, result)
    }

    @Test
    fun `расходы + события вместе → смешаны и отсортированы`() {
        val useCase = BuildTimelineUseCase()
        val expense1 = ExpensesItem(
            id = "1",
            amount = 0.0,
            description = "",
            category = "",
            dateString = "15:00"
        )
        val expense2 = ExpensesItem(
            id = "2",
            amount = 0.0,
            description = "",
            category = "",
            dateString = "22:00"
        )
        val event1 = CalendarEventItem(
            id = "3",
            title = "ZOO",
            timeStamp = 1241255,
            dateString = "",
            timeString = "09:00",
            calendarId = 0L,
            durationMin = 30,
            type = EventType.ENTERTAINMENT,
        )
        val expenses = listOf(expense1, expense2)
        val events = listOf(event1)
        val result = useCase(expenses, events)

        val expected = listOf(
            HubTimelineItem.Event(event1),
            HubTimelineItem.Expense(expense1),
            HubTimelineItem.Expense(expense2),
        )

        assertEquals(expected, result)

    }
}
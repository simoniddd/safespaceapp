package com.example.safespace.feature.hub.domain.interactor

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.EventType
import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.feature.hub.presentation.mapper.TimelineMapper
import com.example.safespace.feature.hub.presentation.model.TimelineItem
import com.example.safespace.feature.hub.presentation.state.EventAggregate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TimelineMapperTest {

    private val mapper = TimelineMapper()

    private fun expense(
        id: String = "x",
        timeStamp: Long = 0L,
        amount: Double = 0.0,
        description: String = "",
    ) = ExpensesItem(
        id = id,
        amount = amount,
        description = description,
        category = "",
        dateString = "",
        timeStamp = timeStamp,
    )

    private fun event(
        id: String = "e",
        timeStamp: Long = 0L,
        title: String = "",
        durationMin: Int = 30,
        type: EventType = EventType.ENTERTAINMENT,
    ) = CalendarEventItem(
        id = id,
        title = title,
        timeStamp = timeStamp,
        dateString = "",
        timeString = "",
        calendarId = 0L,
        durationMin = durationMin,
        type = type,
    )

    @Test
    fun `пустые входы возвращают пустой список`() {
        val result = mapper(
            expenses = emptyList(),
            events = emptyList(),
            steps = null,
            sleep = null,
            exercises = emptyList(),
        )

        assertEquals(emptyList<TimelineItem>(), result)
    }

    @Test
    fun `расходы сортируются по timeStamp`() {
        val later = expense(id = "later", timeStamp = 300L)
        val earlier = expense(id = "earlier", timeStamp = 100L)

        val result = mapper(
            expenses = listOf(later, earlier),
            events = emptyList(),
            steps = null,
            sleep = null,
            exercises = emptyList(),
        )

        assertEquals(listOf(100L, 300L), result.map { it.sortKey })
    }

    @Test
    fun `расходы и события смешиваются и сортируются по timeStamp`() {
        val e1 = expense(id = "e1", timeStamp = 100L)
        val ev = event(id = "ev", timeStamp = 200L)
        val e2 = expense(id = "e2", timeStamp = 300L)

        val result = mapper(
            expenses = listOf(e2, e1),
            events = listOf(ev),
            steps = null,
            sleep = null,
            exercises = emptyList(),
        )

        assertEquals(3, result.size)
        assertTrue(result[0] is TimelineItem.Expense)
        assertTrue(result[1] is TimelineItem.Event)
        assertTrue(result[2] is TimelineItem.Expense)
        assertEquals(listOf(100L, 200L, 300L), result.map { it.sortKey })
    }

    @Test
    fun `дробная сумма траты округляется а не отбрасывает копейки`() {
        val e = expense(amount = 1249.99, timeStamp = 100L)

        val result = mapper(
            expenses = listOf(e),
            events = emptyList(),
            steps = null,
            sleep = null,
            exercises = emptyList(),
        )

        val item = result.single() as TimelineItem.Expense
        assertEquals(1250L, item.amount)
    }

    @Test
    fun `subtitle события без привязанных трат содержит только тип и длительность`() {
        val ev = event(id = "e", timeStamp = 100L, durationMin = 45, type = EventType.WORK)

        val result = mapper(
            expenses = emptyList(),
            events = listOf(ev),
            steps = null,
            sleep = null,
            exercises = emptyList(),
        )

        val item = result.single() as TimelineItem.Event
        assertEquals("WORK · 45 мин", item.subtitle)
    }

    @Test
    fun `subtitle события с агрегатом добавляет число трат сумму и часы`() {
        val ev = event(id = "e", timeStamp = 100L, durationMin = 60, type = EventType.ENTERTAINMENT)
        val aggregate = EventAggregate(
            totalExpense = 45.0,
            expenseCount = 2,
            durationHours = 1.0,
            recurringAverage = null,
        )

        val result = mapper(
            expenses = emptyList(),
            events = listOf(ev),
            eventAggregates = mapOf("e" to aggregate),
            steps = null,
            sleep = null,
            exercises = emptyList(),
        )

        val item = result.single() as TimelineItem.Event
        assertEquals("ENTERTAINMENT · 60 мин · 2 тр · \$45 · 1ч", item.subtitle)
    }

    @Test
    fun `doneCount события подтягивается по id из карты подтверждений`() {
        val ev = event(id = "e-42", timeStamp = 100L)

        val result = mapper(
            expenses = emptyList(),
            events = listOf(ev),
            doneCountByEventId = mapOf("e-42" to 3),
            steps = null,
            sleep = null,
            exercises = emptyList(),
        )

        val item = result.single() as TimelineItem.Event
        assertEquals(3, item.doneCount)
    }

    @Test
    fun `recurringHint события заполняется из среднего по серии если оно есть`() {
        val ev = event(id = "e", timeStamp = 100L)
        val aggregate = EventAggregate(
            totalExpense = 0.0,
            expenseCount = 0,
            durationHours = 1.0,
            recurringAverage = 80.0,
        )

        val result = mapper(
            expenses = emptyList(),
            events = listOf(ev),
            eventAggregates = mapOf("e" to aggregate),
            steps = null,
            sleep = null,
            exercises = emptyList(),
        )

        val item = result.single() as TimelineItem.Event
        assertEquals("обычно ~\$80", item.recurringHint)
    }
}
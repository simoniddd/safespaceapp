package com.example.safespace.feature.hub.domain.interactor

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.core.domain.model.health.DailySteps
import com.example.safespace.core.domain.model.health.ExerciseSession
import com.example.safespace.core.domain.model.health.SleepSummary
import com.example.safespace.feature.hub.presentation.state.EventAggregate
import com.example.safespace.feature.hub.presentation.state.HubTimelineItem

class BuildTimelineUseCase {
    operator fun invoke(
        expenses: List<ExpensesItem>,
        events: List<CalendarEventItem>,
        steps: DailySteps?,
        sleep: SleepSummary?,
        exercises: List<ExerciseSession>
    ): List<HubTimelineItem> {
        val expenseItems = expenses.map { HubTimelineItem.Expense(it) }
        val eventItems = events.map { HubTimelineItem.Event(it) }
        val healthItems = buildList {
            sleep?.let { add(HubTimelineItem.HealthSleep(it)) }
            steps?.let { add(HubTimelineItem.HealthSteps(it)) }
            addAll(exercises.map { HubTimelineItem.HealthExercise(it) })
        }

        return (expenseItems + eventItems + healthItems).sortedBy { it.time }
    }
}

/**
 * In-memory сводка для каждого события: сумма привязанных трат + длительность.
 * Единственный агрегат в day 15 — считаем ЗДЕСЬ, не в Room (Flow уже в памяти,
 * SQL overhead за одну строку не оправдан).
 *
 * @param recurringAveragesByRecurringId результат SQL-агрегации из ExpensesRepository;
 *        подключается по event.recurringEventId. Null для one-off, для recurring —
 *        среднее по прошлым инстансам той же серии.
 */
fun buildEventAggregates(
    expenses: List<ExpensesItem>,
    events: List<CalendarEventItem>,
    recurringAveragesByRecurringId: Map<String, Double>,
): Map<String, EventAggregate> {
    // Один проход по тратам: группируем по eventId. Skip if null.
    val expensesByEventId: Map<String, List<ExpensesItem>> =
        expenses.filter { it.eventId != null }
            .groupBy { it.eventId!! }

    return events.associate { event ->
        val linked = expensesByEventId[event.id].orEmpty()
        event.id to EventAggregate(
            totalExpense = linked.sumOf { it.amount },
            expenseCount = linked.size,
            durationHours = event.durationMin / 60.0,
            recurringAverage = event.recurringEventId?.let {
                recurringAveragesByRecurringId[it]
            },
        )
    }
}

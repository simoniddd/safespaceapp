package com.example.safespace.feature.hub.presentation.mapper

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.core.domain.model.health.DailySteps
import com.example.safespace.core.domain.model.health.ExerciseSession
import com.example.safespace.core.domain.model.health.SleepSummary
import com.example.safespace.feature.hub.presentation.model.TimelineItem
import com.example.safespace.feature.hub.presentation.state.EventAggregate
import com.example.safespace.feature.hub.presentation.util.formatSteps
import com.example.safespace.feature.hub.presentation.util.formatTime
import com.example.safespace.feature.hub.presentation.util.startOfTodayMillis
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.math.roundToLong
import kotlin.time.ExperimentalTime

class TimelineMapper {
    operator fun invoke(
        expenses: List<ExpensesItem>,
        events: List<CalendarEventItem>,
        eventAggregates: Map<String, EventAggregate> = emptyMap(),
        doneCountByEventId: Map<String, Int> = emptyMap(),
        steps: DailySteps?,
        sleep: SleepSummary?,
        exercises: List<ExerciseSession>
    ): List<TimelineItem> {
        val expenseItems = expenses.map(::mapExpense)
        val eventItems = events.map {
            mapEvent(it, eventAggregates[it.id], doneCountByEventId[it.id] ?: 0)
        }
        val healthItems = buildList {
            sleep?.let { add(mapSleep(it)) }
            steps?.let { add(mapSteps(it)) }
            addAll(exercises.map(::mapExercise))
        }

        return (expenseItems + eventItems + healthItems).sortedBy { it.sortKey }
    }

    private fun mapExpense(e: ExpensesItem): TimelineItem.Expense =
        TimelineItem.Expense(
            sortKey = e.timeStamp,
            time = formatTime(e.timeStamp),
            title = e.description,
            amount = e.amount.roundToLong()
        )

    private fun mapEvent(
        e: CalendarEventItem,
        aggregate: EventAggregate?,
        doneCount: Int,
    ): TimelineItem.Event =
        TimelineItem.Event(
            sortKey = e.timeStamp,
            eventId = e.id,
            time = e.timeString,
            instanceStart = e.timeStamp,
            durationMin = e.durationMin,
            title = e.title,
            subtitle = formatEventSubtitle(e, aggregate),
            recurringHint = aggregate?.recurringAverage?.let { "обычно ~\$${"%.0f".format(it)}" },
            intentionState = e.intentionState,
            doneCount = doneCount,
        )

    private fun formatEventSubtitle(e: CalendarEventItem, aggregate: EventAggregate?): String {
        val base = "${e.type.name} · ${e.durationMin} мин"
        return if (aggregate != null && aggregate.expenseCount > 0) {
            val sum = "%.0f".format(aggregate.totalExpense)
            val hours = "%.1f".format(aggregate.durationHours).trimEnd('0').trimEnd('.')
            "$base · ${aggregate.expenseCount} тр · \$$sum · ${hours}ч"
        } else base
    }

    @OptIn(ExperimentalTime::class)
    private fun mapSleep(s: SleepSummary): TimelineItem.HealthDigit =
        TimelineItem.HealthDigit(
            sortKey = startOfTodayMillis(),
            icon = "😴",
            title = "Сон",
            digit = "${s.durationMinutes / 60}ч ${s.durationMinutes % 60}м",
        )

    private fun mapSteps(s: DailySteps): TimelineItem.HealthDigit =
        TimelineItem.HealthDigit(
            sortKey = startOfTodayMillis() + 20L * 3600 * 1000,
            icon = "👟",
            title = "Шаги",
            digit = formatSteps(s.count),
        )

    @OptIn(ExperimentalTime::class)
    private fun mapExercise(e: ExerciseSession): TimelineItem.HealthDigit =
        TimelineItem.HealthDigit(
            sortKey = e.startTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
            icon = "💪",
            title = e.title.ifEmpty { "Тренировка" },
            digit = "${e.durationMinutes} мин",
        )
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

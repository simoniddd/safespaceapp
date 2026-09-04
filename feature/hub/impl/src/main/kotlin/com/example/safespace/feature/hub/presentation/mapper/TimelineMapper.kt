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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlin.math.roundToLong
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TimelineMapper {
    operator fun invoke(
        date: LocalDate,
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
            sleep?.let { add(mapSleep(it, date)) }
            steps?.let { add(mapSteps(it, date)) }
            addAll(exercises.map(::mapExercise))
        }

        return (expenseItems + eventItems + healthItems).sortedBy { it.sortKey }
    }

    private fun mapExpense(e: ExpensesItem): TimelineItem.Expense =
        TimelineItem.Expense(
            sortKey = e.timeStamp,
            stableId = "expense-${e.id}",
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
            stableId = "event-${e.id}-${e.timeStamp}",
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

    private fun mapSleep(s: SleepSummary, date: LocalDate): TimelineItem.HealthDigit =
        TimelineItem.HealthDigit(
            sortKey = startOfDayMillis(date),
            stableId = "health-sleep-$date",
            icon = "😴",
            title = "Сон",
            digit = "${s.durationMinutes / 60}ч ${s.durationMinutes % 60}м",
        )

    private fun mapSteps(s: DailySteps, date: LocalDate): TimelineItem.HealthDigit =
        TimelineItem.HealthDigit(
            sortKey = startOfDayMillis(date) + 20L * 3600 * 1000,
            stableId = "health-steps-$date",
            icon = "👟",
            title = "Шаги",
            digit = formatSteps(s.count),
        )

    private fun mapExercise(e: ExerciseSession): TimelineItem.HealthDigit {
        val sortKey = e.startTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        return TimelineItem.HealthDigit(
            sortKey = sortKey,
            stableId = "health-exercise-$sortKey",
            icon = "💪",
            title = e.title.ifEmpty { "Тренировка" },
            digit = "${e.durationMinutes} мин",
            source = e.source,
        )
    }

    private fun startOfDayMillis(date: LocalDate): Long =
        date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
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

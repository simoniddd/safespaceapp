package com.example.safespace.feature.hub.presentation.mapper

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.core.domain.model.health.DailySteps
import com.example.safespace.core.domain.model.health.ExerciseSession
import com.example.safespace.core.domain.model.health.SleepSummary
import com.example.safespace.feature.hub.presentation.model.TimelineItem
import com.example.safespace.feature.hub.presentation.model.TimelineNode
import com.example.safespace.feature.hub.presentation.state.EventAggregate
import com.example.safespace.feature.hub.presentation.util.formatSteps
import com.example.safespace.feature.hub.presentation.util.formatTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlin.math.roundToLong
import kotlin.time.ExperimentalTime

/** Эвристика: событие >= 24ч считается all-day — такие события в вложении не участвуют. */
private const val ALL_DAY_MIN_DURATION_MIN = 24 * 60

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
        exercises: List<ExerciseSession>,
    ): List<TimelineNode> {
        val eventItemsById = events.associate { ev ->
            ev.id to mapEvent(ev, eventAggregates[ev.id], doneCountByEventId[ev.id] ?: 0)
        }
        val expenseItems = expenses.map(::mapExpense)
        val healthItems = buildList {
            sleep?.let { add(mapSleep(it, date)) }
            steps?.let { add(mapSteps(it, date)) }
            addAll(exercises.map(::mapExercise))
        }

        // 1) Распределяем траты по событиям.
        val nestingResult = nestExpenses(
            expenses = expenseItems.zip(expenses),
            events = events,
            eventItemsById = eventItemsById,
        )

        // 2) Собираем узлы: Leaf для не-вложенных трат / Health / событий без вложений,
        //    EventWithExpenses — для событий, в которые что-то упало.
        val eventNodes = events.map { ev ->
            val eventItem = eventItemsById.getValue(ev.id)
            val nested = nestingResult.nestedByEventId[ev.id].orEmpty()
            if (nested.isEmpty()) TimelineNode.Leaf(eventItem)
            else TimelineNode.EventWithExpenses(eventItem, nested.sortedBy { it.sortKey })
        }
        val expenseNodes = nestingResult.unlinkedExpenses.map { TimelineNode.Leaf(it) }
        val healthNodes = healthItems.map { TimelineNode.Leaf(it) }

        return (eventNodes + expenseNodes + healthNodes).sortedBy { it.sortKey }
    }

    private fun mapExpense(e: ExpensesItem): TimelineItem.Expense =
        TimelineItem.Expense(
            sortKey = e.timeStamp,
            stableId = "expense-${e.id}",
            time = formatTime(e.timeStamp),
            title = e.description,
            amount = e.amount.roundToLong(),
        )

    private fun mapEvent(
        e: CalendarEventItem,
        aggregate: EventAggregate?,
        doneCount: Int,
    ): TimelineItem.Event = TimelineItem.Event(
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
        // «$sum · Nч» часть не рисуем в бандле-витрине — синергия вырезана, оставляем базу.
        return base
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
 * Результат распределения трат по событиям.
 *
 * `nestedByEventId` — траты, которые ушли под конкретное событие (id → список трат).
 * `unlinkedExpenses` — всё, что не привязалось: рисуется в основном потоке.
 */
internal data class NestingResult(
    val nestedByEventId: Map<String, List<TimelineItem.Expense>>,
    val unlinkedExpenses: List<TimelineItem.Expense>,
)

/**
 * Правила вложения трат в события (детерминированно, без синергии):
 *  - если у траты есть `linkedEventId` и такое событие в этом дне → вложить в него;
 *  - иначе проверяем временное пересечение `event.start <= expense.time < event.end`;
 *  - all-day события (durationMin >= 24ч) исключены — иначе все траты уедут в «ДР»;
 *  - если трата попадает в несколько событий — в то, что началось позже
 *    (самое «внутреннее»);
 *  - привязанная трата удаляется из основного потока — никаких дублей.
 */
internal fun nestExpenses(
    expenses: List<Pair<TimelineItem.Expense, ExpensesItem>>,
    events: List<CalendarEventItem>,
    eventItemsById: Map<String, TimelineItem.Event>,
): NestingResult {
    val eligibleEvents = events.filter { it.durationMin < ALL_DAY_MIN_DURATION_MIN }
    if (eligibleEvents.isEmpty()) {
        return NestingResult(
            nestedByEventId = emptyMap(),
            unlinkedExpenses = expenses.map { it.first },
        )
    }

    val eventsById = eligibleEvents.associateBy { it.id }
    val nested = mutableMapOf<String, MutableList<TimelineItem.Expense>>()
    val unlinked = mutableListOf<TimelineItem.Expense>()

    for ((expenseItem, raw) in expenses) {
        val linkedEventId = raw.eventId
        val targetEventId = when {
            linkedEventId != null && eventsById.containsKey(linkedEventId) -> linkedEventId
            else -> findContainingEventId(expense = raw, events = eligibleEvents)
        }
        if (targetEventId != null && eventItemsById.containsKey(targetEventId)) {
            nested.getOrPut(targetEventId) { mutableListOf() } += expenseItem
        } else {
            unlinked += expenseItem
        }
    }

    return NestingResult(nestedByEventId = nested, unlinkedExpenses = unlinked)
}

/**
 * Самое «внутреннее» (началось позже) событие, которое содержит момент траты.
 * Возвращает null, если трата не попадает ни в одно подходящее событие.
 */
private fun findContainingEventId(
    expense: ExpensesItem,
    events: List<CalendarEventItem>,
): String? {
    val t = expense.timeStamp
    return events
        .filter { ev ->
            val end = ev.timeStamp + ev.durationMin * 60_000L
            t in ev.timeStamp until end
        }
        .maxByOrNull { it.timeStamp }
        ?.id
}

/**
 * In-memory сводка для каждого события: сумма привязанных трат + длительность.
 * Остаётся по старому — используется для `pendingQuestion` и будущей аналитики.
 *
 * @param recurringAveragesByRecurringId результат SQL-агрегации из ExpensesRepository.
 */
fun buildEventAggregates(
    expenses: List<ExpensesItem>,
    events: List<CalendarEventItem>,
    recurringAveragesByRecurringId: Map<String, Double>,
): Map<String, EventAggregate> {
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

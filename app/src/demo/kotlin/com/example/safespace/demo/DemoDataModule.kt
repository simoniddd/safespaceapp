package com.example.safespace.demo

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.Emotion
import com.example.safespace.core.domain.model.EventType
import com.example.safespace.core.domain.model.ExpenseConfirmation
import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.core.domain.model.PendingExpenseDraft
import com.example.safespace.core.domain.model.WeatherIcon
import com.example.safespace.core.domain.model.health.DailySteps
import com.example.safespace.core.domain.model.health.ExerciseSession
import com.example.safespace.core.domain.model.health.HealthDataType
import com.example.safespace.core.domain.model.health.HealthDayData
import com.example.safespace.core.domain.model.health.HealthState
import com.example.safespace.core.domain.model.health.SleepSummary
import com.example.safespace.core.domain.model.notification.ParsedTransaction
import com.example.safespace.core.domain.repository.ConfirmResult
import com.example.safespace.core.domain.repository.HealthRepository
import com.example.safespace.core.domain.repository.IntentionRepository
import com.example.safespace.core.domain.repository.PendingExpenseDraftRepository
import com.example.safespace.core.domain.repository.SaveResult
import com.example.safespace.core.domain.repository.WeatherRepository
import com.example.safespace.core.domain.source.EventsSource
import com.example.safespace.core.domain.state.IntentionState
import com.example.safespace.core.domain.worker.IntentionScheduler
import com.example.safespace.feature.calendar.domain.source.CalendarSource
import com.example.safespace.feature.expenses.domain.repository.ExpensesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.dsl.module
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
val demoDataModule = module {
    single<Clock> { DemoClock(TimeZone.currentSystemDefault()) }
    single<ExpensesRepository> { DemoExpensesRepository(clock = get()) }
    single<EventsSource> { DemoEventsSource(clock = get()) }
    single<CalendarSource> { DemoCalendarSource() }
    single<WeatherRepository> { DemoWeatherRepository() }
    single<HealthRepository> { DemoHealthRepository(clock = get()) }
    single<PendingExpenseDraftRepository> { DemoPendingDraftsRepository(clock = get(), expenses = get()) }
    single<IntentionRepository> { DemoIntentionRepository() }
    single<IntentionScheduler> { DemoIntentionScheduler }
}

/**
 * «Сейчас» в демо = сегодняшняя дата + 15:40 в локальном TZ + сколько реально
 * прошло с момента запуска. Так и NowLine сдвигается через минуту, и первое
 * впечатление всегда одинаковое, во сколько бы ни открыли приложение.
 */
@OptIn(ExperimentalTime::class)
class DemoClock(
    private val tz: TimeZone,
    private val real: Clock = Clock.System,
) : Clock {
    private val startedAt: Instant = real.now()
    private val anchor: Instant = real.now()
        .toLocalDateTime(tz)
        .date
        .atTime(15, 40)
        .toInstant(tz)

    override fun now(): Instant = anchor + (real.now() - startedAt)
}

@OptIn(ExperimentalTime::class)
private fun Clock.todayAtTz(tz: TimeZone): LocalDate = now().toLocalDateTime(tz).date

@OptIn(ExperimentalTime::class)
private fun timestampAt(date: LocalDate, hour: Int, minute: Int, tz: TimeZone): Long =
    LocalDateTime(date, LocalTime(hour, minute, 0))
        .toInstant(tz)
        .toEpochMilliseconds()

@OptIn(ExperimentalTime::class)
class DemoExpensesRepository(
    private val clock: Clock,
    private val tz: TimeZone = TimeZone.currentSystemDefault(),
) : ExpensesRepository {
    private val _expenses = MutableStateFlow(seed())

    override fun getAllExpenses(): Flow<List<ExpensesItem>> = _expenses.asStateFlow()

    override suspend fun addExpense(
        amount: Double,
        description: String,
        category: String,
        emotion: Emotion?,
        eventId: String?,
        recurringEventId: String?,
    ) {
        val item = ExpensesItem(
            id = UUID.randomUUID().toString(),
            amount = amount,
            description = description.ifBlank { "Запись" },
            category = category,
            dateString = "",
            timeStamp = clock.now().toEpochMilliseconds(),
            eventId = eventId,
            emotion = emotion,
            recurringEventId = recurringEventId,
        )
        _expenses.value = (_expenses.value + item).sortedBy { it.timeStamp }
    }

    override suspend fun deleteExpense(id: String) {
        _expenses.value = _expenses.value.filterNot { it.id == id }
    }

    override fun observeRecurringSeriesAverages(): Flow<Map<String, Double>> = flowOf(emptyMap())

    internal fun confirmDraftExpense(confirmation: ExpenseConfirmation) {
        val item = ExpensesItem(
            id = UUID.randomUUID().toString(),
            amount = confirmation.amount,
            description = confirmation.merchant.ifBlank { "Трата" },
            category = confirmation.category.name,
            dateString = "",
            timeStamp = confirmation.occurredAt.toEpochMilliseconds(),
        )
        _expenses.value = (_expenses.value + item).sortedBy { it.timeStamp }
    }

    // Лист 1 мокапа, адаптирован под Казахстан: суммы в ₸, одно прошедшее событие
    // «Встреча с Аней» 12:00–14:00 забирает обед и такси внутрь — так на первом
    // экране в 15:40 видно вложенные траты (E).
    private fun seed(): List<ExpensesItem> {
        val today = clock.todayAtTz(tz)
        return listOf(
            demoExpense("exp-1", 1_200.0, "Кофе ☕", "food", today, 8, 40),
            demoExpense("exp-2", 3_800.0, "Обед 🍜", "food", today, 13, 10, eventId = "ev-anya"),
            demoExpense("exp-3", 1_900.0, "Такси 🚕", "transport", today, 13, 30, eventId = "ev-anya"),
        )
    }

    private fun demoExpense(
        id: String,
        amount: Double,
        description: String,
        category: String,
        date: LocalDate,
        hour: Int,
        minute: Int,
        eventId: String? = null,
    ): ExpensesItem = ExpensesItem(
        id = id,
        amount = amount,
        description = description,
        category = category,
        dateString = "",
        timeStamp = timestampAt(date, hour, minute, tz),
        eventId = eventId,
    )
}

@OptIn(ExperimentalTime::class)
class DemoEventsSource(
    private val clock: Clock,
    private val tz: TimeZone = TimeZone.currentSystemDefault(),
) : EventsSource {

    override suspend fun getEventsForDate(date: LocalDate): List<CalendarEventItem> {
        val today = clock.todayAtTz(tz)
        val tomorrow = today.plus(1, DateTimeUnit.DAY)
        return when (date) {
            today -> todaysEvents(today)
            tomorrow -> tomorrowsEvents(tomorrow)
            else -> emptyList()
        }
    }

    private fun todaysEvents(today: LocalDate): List<CalendarEventItem> = listOf(
        CalendarEventItem(
            id = "ev-sprint",
            title = "Планирование спринта",
            timeStamp = timestampAt(today, 10, 0, tz),
            dateString = "",
            timeString = "10:00",
            calendarId = 1L,
            durationMin = 60,
            type = EventType.WORK,
        ),
        CalendarEventItem(
            id = "ev-anya",
            title = "Встреча с Аней",
            timeStamp = timestampAt(today, 12, 0, tz),
            dateString = "",
            timeString = "12:00",
            calendarId = 2L,
            durationMin = 120,
            type = EventType.PERSONAL,
            intentionState = IntentionState.DONE,
        ),
        CalendarEventItem(
            id = "ev-standup",
            title = "Созвон с командой",
            timeStamp = timestampAt(today, 17, 0, tz),
            dateString = "",
            timeString = "17:00",
            calendarId = 1L,
            durationMin = 60,
            type = EventType.WORK,
        ),
        CalendarEventItem(
            id = "ev-dinner",
            title = "Ужин с друзьями",
            timeStamp = timestampAt(today, 19, 0, tz),
            dateString = "",
            timeString = "19:00",
            calendarId = 2L,
            durationMin = 180,
            type = EventType.ENTERTAINMENT,
        ),
    )

    private fun tomorrowsEvents(tomorrow: LocalDate): List<CalendarEventItem> = listOf(
        CalendarEventItem(
            id = "ev-review",
            title = "Code review клиента",
            timeStamp = timestampAt(tomorrow, 11, 0, tz),
            dateString = "",
            timeString = "11:00",
            calendarId = 1L,
            durationMin = 60,
            type = EventType.WORK,
        ),
        CalendarEventItem(
            id = "ev-pool",
            title = "Бассейн",
            timeStamp = timestampAt(tomorrow, 16, 0, tz),
            dateString = "",
            timeString = "16:00",
            calendarId = 2L,
            durationMin = 60,
            type = EventType.SPORT,
        ),
    )
}

class DemoCalendarSource : CalendarSource {
    override val hiddenCalendarIds: Flow<Set<Long>> = flowOf(emptySet())
    override suspend fun setHiddenCalendarIds(ids: Set<Long>) = Unit
}

class DemoWeatherRepository : WeatherRepository {
    private val _weather = MutableStateFlow<WeatherIcon?>(WeatherIcon.PARTLY)
    override fun observeToday(): Flow<WeatherIcon?> = _weather.asStateFlow()
    override suspend fun saveWeather(date: LocalDate, icon: WeatherIcon) {
        _weather.value = icon
    }
}

@OptIn(ExperimentalTime::class)
class DemoHealthRepository(
    private val clock: Clock,
    private val tz: TimeZone = TimeZone.currentSystemDefault(),
) : HealthRepository {

    override fun permissionsFor(types: Set<HealthDataType>): Set<String> =
        types.map { "demo_permission_${it.name}" }.toSet()

    override suspend fun getState(date: LocalDate): HealthState {
        val today = clock.todayAtTz(tz)
        if (date != today) return HealthState.Available(HealthDayData(null, null, emptyList()))

        val runStart = LocalDateTime(today, LocalTime(14, 0, 0))
        return HealthState.Available(
            HealthDayData(
                steps = 6_840,
                sleep = SleepSummary(durationMinutes = 7 * 60 + 20, date = today),
                exercises = listOf(
                    ExerciseSession(
                        durationMinutes = 45,
                        title = "Пробежка",
                        source = "com.strava",
                        startTime = runStart,
                    ),
                ),
            ),
        )
    }
}

@OptIn(ExperimentalTime::class)
class DemoPendingDraftsRepository(
    private val clock: Clock,
    private val expenses: ExpensesRepository,
) : PendingExpenseDraftRepository {

    private val _drafts = MutableStateFlow(seed())

    override fun observeNew(): Flow<List<PendingExpenseDraft>> = _drafts.asStateFlow()

    override suspend fun saveIfNew(pt: ParsedTransaction): SaveResult =
        SaveResult.Saved(draftId = UUID.randomUUID().toString())

    override suspend fun confirm(draftId: String, edits: ExpenseConfirmation): ConfirmResult {
        val removed = _drafts.value.firstOrNull { it.id == draftId } ?: return ConfirmResult.NotFound
        _drafts.value = _drafts.value.filterNot { it.id == draftId }
        (expenses as? DemoExpensesRepository)?.confirmDraftExpense(edits)
        return ConfirmResult.Success(expenseId = removed.id)
    }

    override suspend fun dismiss(draftId: String) {
        _drafts.value = _drafts.value.filterNot { it.id == draftId }
    }

    private fun seed(): List<PendingExpenseDraft> = listOf(
        PendingExpenseDraft(
            id = "draft-taxi",
            amount = 2_400.0,
            currency = "KZT",
            merchant = "Такси",
            occurredAt = clock.now(),
            dedupeKey = "demo-taxi",
            status = com.example.safespace.core.domain.model.DraftStatus.NEW,
        ),
    )
}

class DemoIntentionRepository : IntentionRepository {
    private val _intentions = MutableStateFlow<Map<String, IntentionState>>(
        mapOf("ev-anya" to IntentionState.DONE),
    )

    override fun observeIntentions(): Flow<Map<String, IntentionState>> = _intentions.asStateFlow()
    override fun observeDoneCountByEventId(): Flow<Map<String, Int>> = flowOf(mapOf("ev-anya" to 3))
    override suspend fun markEventAsIntention(eventId: String) {
        _intentions.value = _intentions.value + (eventId to IntentionState.UPCOMING)
    }
    override suspend fun isEventTagged(eventId: String): Boolean = _intentions.value.containsKey(eventId)
    override suspend fun insertPendingIfAbsent(eventId: String, instanceStart: Long) = Unit
    override suspend fun getStatus(eventId: String, instanceStart: Long): IntentionState? =
        _intentions.value[eventId]
    override suspend fun updateStatus(eventId: String, instanceStart: Long, newStatus: IntentionState) {
        _intentions.value = _intentions.value + (eventId to newStatus)
    }
}

object DemoIntentionScheduler : IntentionScheduler {
    override fun scheduleIntentionCheck(eventId: String, instanceStart: Long, delayMillis: Long) = Unit
}

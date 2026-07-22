package com.example.safespace.core.domain.usecase

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.health.ExerciseSession
import com.example.safespace.core.domain.state.IntentionState
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime

data class IntentionAutoConfirmation(
    val eventId: String,
    val instanceStart: Long,
)

class AutoConfirmIntentionsUseCase {

    @OptIn(ExperimentalTime::class)
    operator fun invoke(
        events: List<CalendarEventItem>,
        exercises: List<ExerciseSession>,
        timeZone: TimeZone,
    ): List<IntentionAutoConfirmation> {
        if (events.isEmpty() || exercises.isEmpty()) return emptyList()

        val exerciseWindows = exercises.map { session ->
            val start = session.startTime.toInstant(timeZone).toEpochMilliseconds()
            val end = start + session.durationMinutes * MILLIS_PER_MINUTE
            start to end
        }

        return events
            .asSequence()
            .filter { it.intentionState in CONFIRMABLE_STATES }
            .filter { event ->
                val eventStart = event.timeStamp
                val eventEnd = eventStart + event.durationMin * MILLIS_PER_MINUTE
                exerciseWindows.any { (exStart, exEnd) ->
                    exStart < eventEnd && exEnd > eventStart
                }
            }
            .map { IntentionAutoConfirmation(eventId = it.id, instanceStart = it.timeStamp) }
            .toList()
    }

    private companion object {
        const val MILLIS_PER_MINUTE = 60_000L
        val CONFIRMABLE_STATES = setOf(IntentionState.UPCOMING, IntentionState.PENDING)
    }
}

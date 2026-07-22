package com.example.safespace.core.domain.usecase

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.EventType
import com.example.safespace.core.domain.model.health.ExerciseSession
import com.example.safespace.core.domain.state.IntentionState
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class AutoConfirmIntentionsUseCaseTest {

    private val useCase = AutoConfirmIntentionsUseCase()
    private val utc = TimeZone.UTC

    @Test
    fun `empty inputs return empty list`() {
        assertTrue(useCase(emptyList(), emptyList(), utc).isEmpty())
    }

    @Test
    fun `no exercises returns empty list even for tagged events`() {
        val event = event(id = "e1", startAt = "2026-07-22T19:00", durationMin = 60, state = IntentionState.PENDING)
        assertTrue(useCase(listOf(event), emptyList(), utc).isEmpty())
    }

    @Test
    fun `untagged event is skipped`() {
        val event = event(id = "e1", startAt = "2026-07-22T19:00", durationMin = 60, state = null)
        val ex = exercise(startAt = "2026-07-22T19:10", durationMin = 40)
        assertTrue(useCase(listOf(event), listOf(ex), utc).isEmpty())
    }

    @Test
    fun `DONE event is not re-confirmed`() {
        val event = event(id = "e1", startAt = "2026-07-22T19:00", durationMin = 60, state = IntentionState.DONE)
        val ex = exercise(startAt = "2026-07-22T19:10", durationMin = 40)
        assertTrue(useCase(listOf(event), listOf(ex), utc).isEmpty())
    }

    @Test
    fun `SKIPPED event is respected and not confirmed`() {
        val event = event(id = "e1", startAt = "2026-07-22T19:00", durationMin = 60, state = IntentionState.SKIPPED)
        val ex = exercise(startAt = "2026-07-22T19:10", durationMin = 40)
        assertTrue(useCase(listOf(event), listOf(ex), utc).isEmpty())
    }

    @Test
    fun `UPCOMING event with fully-contained exercise is confirmed`() {
        val event = event(id = "e1", startAt = "2026-07-22T19:00", durationMin = 60, state = IntentionState.UPCOMING)
        val ex = exercise(startAt = "2026-07-22T19:10", durationMin = 40)
        val result = useCase(listOf(event), listOf(ex), utc)
        assertEquals(1, result.size)
        assertEquals("e1", result.first().eventId)
    }

    @Test
    fun `PENDING event with overlapping exercise is confirmed`() {
        val event = event(id = "e1", startAt = "2026-07-22T19:00", durationMin = 60, state = IntentionState.PENDING)
        val ex = exercise(startAt = "2026-07-22T19:10", durationMin = 40)
        val result = useCase(listOf(event), listOf(ex), utc)
        assertEquals(listOf("e1"), result.map { it.eventId })
    }

    @Test
    fun `exercise starting before event but overlapping is confirmed`() {
        val event = event(id = "e1", startAt = "2026-07-22T19:00", durationMin = 60, state = IntentionState.UPCOMING)
        val ex = exercise(startAt = "2026-07-22T18:50", durationMin = 40)
        val result = useCase(listOf(event), listOf(ex), utc)
        assertEquals(listOf("e1"), result.map { it.eventId })
    }

    @Test
    fun `exercise ending exactly at event start does not confirm (touching, not overlapping)`() {
        val event = event(id = "e1", startAt = "2026-07-22T19:00", durationMin = 60, state = IntentionState.UPCOMING)
        val ex = exercise(startAt = "2026-07-22T18:00", durationMin = 60)
        assertTrue(useCase(listOf(event), listOf(ex), utc).isEmpty())
    }

    @Test
    fun `exercise starting exactly at event end does not confirm (touching, not overlapping)`() {
        val event = event(id = "e1", startAt = "2026-07-22T19:00", durationMin = 60, state = IntentionState.UPCOMING)
        val ex = exercise(startAt = "2026-07-22T20:00", durationMin = 30)
        assertTrue(useCase(listOf(event), listOf(ex), utc).isEmpty())
    }

    @Test
    fun `midnight-crossing overlap is confirmed`() {
        val event = event(id = "e1", startAt = "2026-07-22T23:30", durationMin = 60, state = IntentionState.PENDING)
        val ex = exercise(startAt = "2026-07-23T00:00", durationMin = 60)
        val result = useCase(listOf(event), listOf(ex), utc)
        assertEquals(listOf("e1"), result.map { it.eventId })
    }

    @Test
    fun `multiple exercises overlapping same event produce one confirmation`() {
        val event = event(id = "e1", startAt = "2026-07-22T19:00", durationMin = 60, state = IntentionState.UPCOMING)
        val ex1 = exercise(startAt = "2026-07-22T19:00", durationMin = 20)
        val ex2 = exercise(startAt = "2026-07-22T19:30", durationMin = 20)
        val result = useCase(listOf(event), listOf(ex1, ex2), utc)
        assertEquals(1, result.size)
    }

    @Test
    fun `multiple events each matched by their own exercise get separate confirmations`() {
        val morning = event(id = "m", startAt = "2026-07-22T08:00", durationMin = 60, state = IntentionState.UPCOMING)
        val evening = event(id = "v", startAt = "2026-07-22T19:00", durationMin = 60, state = IntentionState.PENDING)
        val exMorning = exercise(startAt = "2026-07-22T08:10", durationMin = 40)
        val exEvening = exercise(startAt = "2026-07-22T19:10", durationMin = 40)

        val result = useCase(listOf(morning, evening), listOf(exMorning, exEvening), utc)

        assertEquals(setOf("m", "v"), result.map { it.eventId }.toSet())
    }

    private fun event(
        id: String,
        startAt: String,
        durationMin: Int,
        state: IntentionState?,
    ): CalendarEventItem {
        val start = LocalDateTime.parse(startAt).toInstant(utc).toEpochMilliseconds()
        return CalendarEventItem(
            id = id,
            title = "Event $id",
            timeStamp = start,
            dateString = "test",
            timeString = "test",
            calendarId = 1L,
            durationMin = durationMin,
            type = EventType.SPORT,
            intentionState = state,
        )
    }

    private fun exercise(startAt: String, durationMin: Int): ExerciseSession = ExerciseSession(
        durationMinutes = durationMin,
        title = "test",
        source = "test",
        startTime = LocalDateTime.parse(startAt),
    )
}

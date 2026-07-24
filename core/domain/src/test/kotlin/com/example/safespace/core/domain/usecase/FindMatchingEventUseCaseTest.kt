package com.example.safespace.core.domain.usecase

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.EventType
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class FindMatchingEventUseCaseTest {

    private val useCase = FindMatchingEventUseCase()
    private val utc = TimeZone.UTC

    @Test
    fun `E1 - empty events list returns empty result`() {
        assertTrue(useCase(timeMillis("2026-07-24T12:30"), emptyList()).isEmpty())
    }

    @Test
    fun `A1 - single event matching expense in middle`() {
        val meeting = event(id = "m1", startAt = "2026-07-24T12:00", durationMin = 60)
        val result = useCase(timeMillis("2026-07-24T12:30"), listOf(meeting))

        assertEquals(listOf(meeting), result)
        assertEquals(listOf("m1"), result.map { it.id })
    }

    @Test
    fun `A2 - expense before event start returns empty (buffer only applies after)`() {
        val meeting = event(id = "m1", startAt = "2026-07-24T12:00", durationMin = 60)
        val result = useCase(timeMillis("2026-07-24T11:00"), listOf(meeting))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `B1 - expense exactly at event start is included (left boundary inclusive)`() {
        val meeting = event(id = "m1", startAt = "2026-07-24T12:00", durationMin = 60)
        val result = useCase(timeMillis("2026-07-24T12:00"), listOf(meeting))
        assertEquals(listOf("m1"), result.map { it.id })
    }

    @Test
    fun `B2 - expense exactly at event end is included (buffer starts here)`() {
        val meeting = event(id = "m1", startAt = "2026-07-24T12:00", durationMin = 60)
        val result = useCase(timeMillis("2026-07-24T13:00"), listOf(meeting))
        assertEquals(listOf("m1"), result.map { it.id })
    }

    @Test
    fun `B3 - expense at event end + buffer is excluded (right boundary exclusive)`() {
        val meeting = event(id = "m1", startAt = "2026-07-24T12:00", durationMin = 60)
        val result = useCase(timeMillis("2026-07-24T14:00"), listOf(meeting))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `B4 - events with same start are ordered by id (stable tie-breaker)`() {
        val work = event(id = "1", startAt = "2026-07-24T12:00", durationMin = 60)
        val personal = event(id = "2", startAt = "2026-07-24T12:00", durationMin = 120)
        // input reversed to prove sort is not "as-came" order
        val result = useCase(timeMillis("2026-07-24T12:30"), listOf(personal, work))
        assertEquals(listOf("1", "2"), result.map { it.id })
    }

    @Test
    fun `C1 - expense inside buffer zone (not on boundary) is included`() {
        val meeting = event(id = "m1", startAt = "2026-07-24T12:00", durationMin = 60)
        val result = useCase(timeMillis("2026-07-24T13:43"), listOf(meeting))
        assertEquals(listOf("m1"), result.map { it.id })
    }

    @Test
    fun `D1 - back-to-back events with expense in overlap zone returns both sorted by start`() {
        val coffee = event(id = "1", startAt = "2026-07-24T11:00", durationMin = 60)
        val meeting = event(id = "2", startAt = "2026-07-24T12:00", durationMin = 60)
        // input reversed to prove sort by start, not by relevance to expense time
        val result = useCase(timeMillis("2026-07-24T12:30"), listOf(meeting, coffee))
        assertEquals(listOf("1", "2"), result.map { it.id })
    }

    @Test
    fun `D2 - nested events both match, sorted by start`() {
        val conference = event(id = "1", startAt = "2026-07-24T10:00", durationMin = 240)
        val coffeeBreak = event(id = "2", startAt = "2026-07-24T11:00", durationMin = 60)
        val result = useCase(timeMillis("2026-07-24T11:30"), listOf(coffeeBreak, conference))
        assertEquals(listOf("1", "2"), result.map { it.id })
    }

    @Test
    fun `E2 - midnight-crossing event matches expense in the early hours of next day`() {
        // Party 23:00 durationMin=90 => ends 2026-07-25T00:30; expense at 00:15 = inside event
        // If we naively used LocalTime (no date) this would break — Long/Instant makes it work.
        val party = event(id = "p1", startAt = "2026-07-24T23:00", durationMin = 90)
        val result = useCase(timeMillis("2026-07-25T00:15"), listOf(party))
        assertEquals(listOf("p1"), result.map { it.id })
    }

    private fun event(
        id: String,
        startAt: String,
        durationMin: Int,
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
            type = EventType.PERSONAL,
        )
    }

    private fun timeMillis(at: String): Long =
        LocalDateTime.parse(at).toInstant(utc).toEpochMilliseconds()
}
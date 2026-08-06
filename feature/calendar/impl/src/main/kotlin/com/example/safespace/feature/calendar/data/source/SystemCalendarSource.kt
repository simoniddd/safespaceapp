package com.example.safespace.feature.calendar.data.source

import android.content.ContentUris
import android.content.Context
import android.provider.CalendarContract
import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.EventType
import com.example.safespace.core.domain.source.EventsSource
import java.text.SimpleDateFormat
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlin.time.ExperimentalTime
import java.util.Date
import java.util.Locale

class SystemCalendarSource(
    private val context: Context
) : EventsSource {
    @OptIn(ExperimentalTime::class)
    override suspend fun getEventsForDate(date: LocalDate): List<CalendarEventItem> {
        val tz = TimeZone.currentSystemDefault()
        val startTimeMillis = date.atStartOfDayIn(tz).toEpochMilliseconds()
        val endTimeMillis = date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(tz).toEpochMilliseconds()

        val uri = CalendarContract.Instances.CONTENT_URI.buildUpon().also { builder ->
            ContentUris.appendId(builder, startTimeMillis)
            ContentUris.appendId(builder, endTimeMillis)
        }.build()

        // Instances — view поверх Events, поэтому Events.RRULE читается без JOIN.
        // EVENT_ID у recurring instances один и тот же — это и есть series id.
        val projection = arrayOf(
            CalendarContract.Instances._ID,          // 0 — id инстанса (per-occurrence)
            CalendarContract.Instances.TITLE,        // 1
            CalendarContract.Instances.BEGIN,        // 2
            CalendarContract.Instances.END,          // 3
            CalendarContract.Events.CALENDAR_ID,     // 4
            CalendarContract.Instances.EVENT_ID,     // 5 — parent Events._ID (общий у серии)
            CalendarContract.Events.RRULE,           // 6 — iCal recurrence rule; null = one-off
        )

        val cursor = try {
            context.contentResolver.query(uri, projection, null, null, null)
        } catch (e: SecurityException) {
            return emptyList()
        }

        val result = mutableListOf<CalendarEventItem>()

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getString(0)
                val title = it.getString(1)
                val begin = it.getLong(2)
                val end = it.getLong(3)
                val calendarId = it.getLong(4)
                val parentEventId = it.getString(5)
                val rrule = it.getString(6)
                val durationMin = ((end - begin) / 60_000).toInt()
                val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(begin))
                val dateString = ""
                val type = EventType.PERSONAL

                // Recurring iff у события есть RRULE. EVENT_ID у одноразовых событий
                // тоже валидный, но семантически «серии» нет — записываем null.
                val recurringEventId =
                    if (rrule.isNullOrBlank()) null else parentEventId

                result.add(
                    CalendarEventItem(
                        id = id,
                        title = title,
                        timeStamp = begin,
                        dateString = dateString,
                        timeString = timeString,
                        calendarId = calendarId,
                        durationMin = durationMin,
                        type = type,
                        recurringEventId = recurringEventId,
                    )
                )
            }
        }
        return result
    }
}

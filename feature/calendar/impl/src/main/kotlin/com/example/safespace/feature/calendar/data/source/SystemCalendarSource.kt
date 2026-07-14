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

        val projection = arrayOf(
            CalendarContract.Instances._ID,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Events.CALENDAR_ID,
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
                val durationMin = ((end - begin) / 60_000).toInt()
                val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(begin))
                val dateString = ""
                val type = EventType.PERSONAL

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
                    )
                )
            }
        }
        return result
    }
}
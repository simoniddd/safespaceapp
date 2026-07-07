package com.example.safespace.feature.calendar.data.source

import android.content.ContentUris
import android.content.Context
import android.provider.CalendarContract
import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.EventType
import com.example.safespace.core.domain.source.EventsSource
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

class SystemCalendarSource(
    private val context: Context
) : EventsSource {
    override suspend fun getEventsForDate(date: LocalDate): List<CalendarEventItem> {
        val startTimeMillis =
            date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val endTimeMillis =
            date.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()

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
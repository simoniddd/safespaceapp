package com.example.safespace.feature.calendar.data.source

import android.content.Context
import android.provider.CalendarContract
import com.example.safespace.core.domain.model.CalendarInfo

class SystemCalendarsSource(private val context: Context) {

    suspend fun getCalendars(): List<CalendarInfo> {
        val result = mutableListOf<CalendarInfo>()
        val cursor = context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR
            ),
            null, null, null
        ) ?: return emptyList()

        cursor.use {
            while (cursor.moveToNext()) {
                // твой код: читаешь 3 колонки и добавляешь в result
                val id = cursor.getLong(0)
                val name = cursor.getString(1)
                val color = cursor.getInt(2)

                result.add(
                    CalendarInfo(
                        id = id,
                        name = name,
                        color = color
                    )
                )
            }
        }
        return result
    }
}
package com.example.safespace.feature.calendar.data.repository

import com.example.safespace.core.data.local.dao.CalendarDao
import com.example.safespace.core.data.local.entity.CalendarEventEntity
import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.EventType
import com.example.safespace.feature.calendar.domain.repository.CalendarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.UUID

class CalendarRepositoryImpl(
    private val calendarDao: CalendarDao
) : CalendarRepository {

    override fun getEventsForDate(date: LocalDate): Flow<List<CalendarEventItem>> {
        return calendarDao.getEventsForDate(date.toString()).map { events ->
            events.map { event ->
                CalendarEventItem(
                    id = event.id,
                    title = event.title,
                    timeStamp = event.timeStamp,
                    dateString = event.dateString,
                    timeString = event.timeString,
                    durationMin = event.durationMin,
                    type = EventType.valueOf(event.type)
                )
            }
        }
    }

    override suspend fun addEvent(
        title: String,
        timeStamp: Long,
        dateString: String,
        timeString: String,
        durationMin: Int,
        type: EventType
    ) {
        calendarDao.addEvent(
            CalendarEventEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                timeStamp = timeStamp,
                dateString = dateString,
                timeString = timeString,
                durationMin = durationMin,
                type = type.name
            )
        )
    }
}
package com.example.safespace.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.safespace.core.data.local.entity.CalendarEventEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface CalendarDao {

    @Query("SELECT * FROM calendar_events WHERE dateString = :date ORDER BY timeStamp ASC")
    fun getEventsForDate(date: LocalDate): Flow<List<CalendarEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEvent(event: CalendarEventEntity)
}
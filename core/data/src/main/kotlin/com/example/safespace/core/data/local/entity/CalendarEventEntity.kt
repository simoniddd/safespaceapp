package com.example.safespace.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val timeStamp: Long,
    val dateString: String,
    val timeString: String,
    val durationMin: Int,
    val type: String
)



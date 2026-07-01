package com.example.safespace.feature.calendar.domain.model

data class CalendarEventItem(
    val id: String,
    val title: String,
    val timeStamp: Long,
    val dateString: String, // "Сегодня, 1 июля"
    val timeString: String, // "19:00"
    val durationMin: Int,
    val type: EventType
)
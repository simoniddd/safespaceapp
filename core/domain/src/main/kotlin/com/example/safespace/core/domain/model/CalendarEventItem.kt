package com.example.safespace.core.domain.model

data class CalendarEventItem(
    val id: String,
    val title: String,
    val timeStamp: Long,
    val dateString: String, // "Сегодня, 1 июля"
    val timeString: String, // "19:00"
    val durationMin: Int,
    val type: EventType
)
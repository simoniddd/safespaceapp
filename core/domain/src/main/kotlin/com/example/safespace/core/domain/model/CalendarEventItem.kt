package com.example.safespace.core.domain.model

import com.example.safespace.core.domain.state.IntentionState

data class CalendarEventItem(
    val id: String,
    val title: String,
    val timeStamp: Long,
    val dateString: String, // "Сегодня, 1 июля"
    val timeString: String, // "19:00"
    val calendarId: Long,
    val durationMin: Int,
    val type: EventType,
    val intentionState: IntentionState? = null,
)
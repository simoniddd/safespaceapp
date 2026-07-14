package com.example.safespace.feature.hub.presentation.state

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.core.domain.model.WeatherIcon

sealed interface HubTimelineItem {
    val id: String
    val time: String

    data class Expense(val item: ExpensesItem) : HubTimelineItem {
        override val id = item.id
        override val time = item.dateString
    }

    data class Event(val item: CalendarEventItem) : HubTimelineItem {
        override val id = item.id
        override val time = item.timeString
    }
}

sealed interface PendingQuestion {
    data class IntentionCheck(
        val eventId: String,
        val instanceStart: Long,
        val eventTitle: String,
    ) : PendingQuestion
}

data class HubState(
    val isLoading: Boolean = true,
    val dailyExpenses: Double = 0.0,
    val upcomingEventsCount: Int = 0,
    val userName: String = "User",
    val todayIcon: WeatherIcon? = null,
    val timelineItems: List<HubTimelineItem> = emptyList(),
    val pendingQuestion: PendingQuestion? = null,
    val doneCountByEventId: Map<String, Int> = emptyMap(),
)

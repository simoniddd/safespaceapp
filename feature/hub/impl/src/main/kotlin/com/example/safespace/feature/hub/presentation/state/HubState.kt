package com.example.safespace.feature.hub.presentation.state

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.ExpensesItem

// Универсальный элемент ленты
sealed interface HubTimelineItem {
    val id: String
    val time: String

    data class Expense(val item: ExpensesItem) : HubTimelineItem {
        override val id = item.id
        override val time = item.dateString // Пока берем дату, потом докрутим время
    }

    data class Event(val item: CalendarEventItem) : HubTimelineItem {
        override val id = item.id
        override val time = item.timeString
    }
}

data class HubState(
    val isLoading: Boolean = true,
    val dailyExpenses: Double = 0.0,
    val upcomingEventsCount: Int = 0,
    val userName: String = "User",
    val timelineItems: List<HubTimelineItem> = emptyList()
)

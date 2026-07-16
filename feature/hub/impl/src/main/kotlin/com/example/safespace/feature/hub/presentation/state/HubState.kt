package com.example.safespace.feature.hub.presentation.state

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.core.domain.model.WeatherIcon
import com.example.safespace.core.domain.model.health.DailySteps
import com.example.safespace.core.domain.model.health.ExerciseSession
import com.example.safespace.core.domain.model.health.SleepSummary

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

    data class HealthSleep(val item: SleepSummary) : HubTimelineItem {
        override val id = "health_sleep_${item.date}"
        override val time = "00:00"
    }

    data class HealthSteps(val item: DailySteps) : HubTimelineItem {
        override val id = "health_steps_${item.date}"
        override val time = "20:00"
    }

    data class HealthExercise(val item: ExerciseSession) : HubTimelineItem {
        override val id = "health_exercise_${item.startTime}"
        override val time = "${item.startTime.hour.toString().padStart(2, '0')}:${item.startTime.minute.toString().padStart(2, '0')}"
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

package com.example.safespace.core.domain.model

import com.example.safespace.core.domain.model.health.ExerciseSession
import com.example.safespace.core.domain.model.health.HealthDayData
import com.example.safespace.core.domain.model.health.SleepSummary
import kotlinx.datetime.LocalDate
import java.util.spi.ResourceBundleControlProvider

data class DayRecap(
    val date: LocalDate,
    val weather: DayWeather?,
    val sleep: SleepSummary?,
    val steps: Int?,
    val morning: List<RecapItem>,
    val day: List<RecapItem>,
    val evening: List<RecapItem>,
) {
    val isSilent: Boolean =
        morning.isEmpty() && day.isEmpty() && evening.isEmpty()
                && weather == null && sleep == null && steps == null
}

sealed interface RecapItem {
    val at: Long
    data class Expense(val expense: ExpensesItem) : RecapItem{
        override val at: Long
            get() = expense.timeStamp
    }
    data class Event(val event: CalendarEventItem) : RecapItem {
        override val at: Long
            get() = event.timeStamp
    }
    data class Exercise(val session: ExerciseSession): RecapItem {
        override val at: Long
            get() = session
    }
}
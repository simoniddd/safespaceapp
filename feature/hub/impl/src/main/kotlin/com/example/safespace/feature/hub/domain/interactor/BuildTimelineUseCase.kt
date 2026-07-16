package com.example.safespace.feature.hub.domain.interactor

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.core.domain.model.health.DailySteps
import com.example.safespace.core.domain.model.health.ExerciseSession
import com.example.safespace.core.domain.model.health.SleepSummary
import com.example.safespace.feature.hub.presentation.state.HubTimelineItem

class BuildTimelineUseCase {
    operator fun invoke(
        expenses: List<ExpensesItem>,
        events: List<CalendarEventItem>,
        steps: DailySteps?,
        sleep: SleepSummary?,
        exercises: List<ExerciseSession>
    ): List<HubTimelineItem> {
        val expenseItems = expenses.map { HubTimelineItem.Expense(it) }
        val eventItems = events.map { HubTimelineItem.Event(it) }
        val healthItems = buildList {
            sleep?.let { add(HubTimelineItem.HealthSleep(it)) }
            steps?.let { add(HubTimelineItem.HealthSteps(it)) }
            addAll(exercises.map { HubTimelineItem.HealthExercise(it) })
        }

        return (expenseItems + eventItems + healthItems).sortedBy { it.time }
    }
}
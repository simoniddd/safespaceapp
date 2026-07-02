package com.example.safespace.feature.hub.domain.interactor

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.feature.hub.presentation.state.HubTimelineItem

class BuildTimelineUseCase {
    operator fun invoke(
        expenses: List<ExpensesItem>,
        events: List<CalendarEventItem>
    ): List<HubTimelineItem> {
        val expenseItems = expenses.map { HubTimelineItem.Expense(it) }
        val eventItems = events.map { HubTimelineItem.Event(it) }

        return(expenseItems + eventItems).sortedBy { it.time }
    }
}
package com.example.safespace.feature.hub.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.safespace.core.presentation.StatefulViewModel
import com.example.safespace.core.presentation.navigation.Navigator
import com.example.safespace.feature.calendar.domain.repository.CalendarRepository
import com.example.safespace.feature.calendar.navigation.CalendarEntryPoint
import com.example.safespace.feature.expenses.domain.repository.ExpensesRepository
import com.example.safespace.feature.expenses.navigation.ExpensesEntryPoint
import com.example.safespace.feature.hub.domain.interactor.BuildTimelineUseCase
import com.example.safespace.feature.hub.presentation.state.HubState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HubViewModel(
    private val expensesRepository: ExpensesRepository,
    private val calendarRepository: CalendarRepository,
    private val expensesEntryPoint: ExpensesEntryPoint,
    private val calendarEntryPoint: CalendarEntryPoint,
    navigator: Navigator
) : StatefulViewModel<HubState>(HubState(), navigator) {

    private val buildTimeLine = BuildTimelineUseCase()

    init {
        loadHubData()
    }

    fun onExpensesClicked() {
        navigateTo(expensesEntryPoint.expensesRoute())
    }

    fun onCalendarClicked() {
        navigateTo(calendarEntryPoint.calendarRoute())
    }

    private fun loadHubData() {
        viewModelScope.launch {
            combine(
                expensesRepository.getAllExpenses(),
                calendarRepository.getEventsForDate("Сегодня, 1 июля")
            ) { expenses, events ->
                val totalSpent = expenses.sumOf { it.amount }
                val timeLineItems = buildTimeLine(expenses, events)
                Pair(totalSpent, timeLineItems)
            }.collect { (totalSpent, combinedAndSortedItems) ->
                updateState {
                    copy(
                        isLoading = false,
                        dailyExpenses = totalSpent,
                        timelineItems = combinedAndSortedItems
                    )
                }
            }
        }
    }
}

package com.example.safespace.feature.hub.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.safespace.core.presentation.StatefulViewModel
import com.example.safespace.core.presentation.navigation.Navigator
import com.example.safespace.feature.expenses.domain.repository.ExpensesRepository
import com.example.safespace.feature.expenses.navigation.ExpensesEntryPoint
import com.example.safespace.feature.hub.presentation.state.HubState
import com.example.safespace.feature.hub.presentation.state.HubTimelineItem
import kotlinx.coroutines.launch

class HubViewModel(
    private val expensesRepository: ExpensesRepository,
    private val expensesEntryPoint: ExpensesEntryPoint,
    navigator: Navigator
) : StatefulViewModel<HubState>(HubState(), navigator) {

    init {
        loadHubData()
    }

    fun onExpensesClicked() {
        navigateTo(expensesEntryPoint.expensesRoute())
    }

    private fun loadHubData() {
        viewModelScope.launch {
            expensesRepository.getAllExpenses().collect { expenses ->
                val totalSpent = expenses.sumOf { it.amount }
                val items = expenses.map { HubTimelineItem.Expense(it) }

                updateState {
                    copy(
                        isLoading = false,
                        dailyExpenses = totalSpent,
                        timelineItems = items
                    )
                }
            }
        }
    }
}

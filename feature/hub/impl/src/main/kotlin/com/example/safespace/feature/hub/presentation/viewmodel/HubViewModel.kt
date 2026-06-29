package com.example.safespace.feature.hub.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.safespace.core.presentation.StatefulViewModel
import com.example.safespace.feature.expenses.domain.repository.ExpensesRepository
import com.example.safespace.feature.hub.presentation.state.HubState
import kotlinx.coroutines.launch

class HubViewModel(
    private val expensesRepository: ExpensesRepository
) : StatefulViewModel<HubState>(HubState()) {

    init {
        loadHubData()
    }

    private fun loadHubData() {
        viewModelScope.launch {
            expensesRepository.getAllExpenses().collect { expenses ->
                val totalSpent = expenses.sumOf { it.amount }

                updateState {
                    copy(isLoading = false, dailyExpenses = totalSpent)
                }
            }
        }
    }
}

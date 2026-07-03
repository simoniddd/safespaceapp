package com.example.safespace.feature.expenses.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.core.presentation.StatefulViewModel
import com.example.safespace.core.presentation.navigation.Navigator
import com.example.safespace.feature.expenses.domain.repository.ExpensesRepository
import com.example.safespace.feature.expenses.presentation.action.ExpensesUiEvent
import com.example.safespace.feature.expenses.presentation.state.ExpensesState
import kotlinx.coroutines.launch

class ExpensesViewModel(
    private val repository: ExpensesRepository,
    navigator: Navigator
) : StatefulViewModel<ExpensesState>(ExpensesState(), navigator) {

    private var allExpenses: List<ExpensesItem> = emptyList()

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            repository.getAllExpenses().collect { expensesList ->
                allExpenses = expensesList
                applyFilters()
            }
        }
    }

    fun onEvent(event: ExpensesUiEvent) {
        when (event) {
            is ExpensesUiEvent.OnAmountChanged -> updateState { copy(inputAmount = event.amount) }
            is ExpensesUiEvent.OnDescriptionChanged -> updateState { copy(inputDescription = event.description) }
            is ExpensesUiEvent.OnCategorySelected -> updateState { copy(selectedCategory = event.category) }
            is ExpensesUiEvent.OnEmotionSelected -> updateState { copy(selectedEmotion = event.emotion) }
            is ExpensesUiEvent.OnAddExpenseClicked -> addExpense()
            is ExpensesUiEvent.OnDeleteExpenseClicked -> deleteExpense(event.id)
            is ExpensesUiEvent.OnToggleAddSheet -> updateState { copy(isAddSheetOpened = event.isOpen) }
            is ExpensesUiEvent.OnFilterChanged -> {
                updateState { copy(currentFilter = event.category) }
                applyFilters()
            }
        }
    }

    private fun addExpense() {
        val amount = state.value.inputAmount.toDoubleOrNull() ?: return
        val description = state.value.inputDescription.ifBlank { "Без описания" }
        val category = state.value.selectedCategory.displayName
        val emotion = state.value.selectedEmotion

        updateState { copy(inputAmount = "", inputDescription = "") }
        viewModelScope.launch {
            repository.addExpense(amount, description, category, emotion)
        }
    }

    private fun deleteExpense(id: String) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }

    private fun applyFilters() {
        val currentFilter = state.value.currentFilter
        val filtered = if (currentFilter == null) allExpenses
        else allExpenses.filter { it.category == currentFilter.displayName }

        val grouped = filtered.groupBy { it.dateString }
        val categoryTotals = filtered
            .groupBy { it.category }
            .map { (cat, items) -> cat to items.sumOf { it.amount } }
            .sortedByDescending { it.second }

        updateState {
            copy(
                expenses = filtered,
                totalSum = filtered.sumOf { it.amount },
                groupedExpenses = grouped,
                categoryTotals = categoryTotals
            )
        }
    }
}

package com.example.safespace.feature.expenses.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safespace.feature.expenses.domain.model.ExpensesItem
import com.example.safespace.feature.expenses.domain.repository.ExpensesRepository
import com.example.safespace.feature.expenses.presentation.action.ExpensesUiEvent
import com.example.safespace.feature.expenses.presentation.state.ExpensesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpensesViewModel(
    private val repository: ExpensesRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ExpensesState())
    val state: StateFlow<ExpensesState> = _state.asStateFlow()

    private var allExpenses: List<ExpensesItem> = emptyList()

    init {
        viewModelScope.launch {
            repository.getAllExpenses().collect { expensesList ->
                allExpenses = expensesList
                applyFilters()
            }
        }
    }

    fun onEvent(event: ExpensesUiEvent) {
        when (event) {
            is ExpensesUiEvent.OnAmountChanged -> _state.update { it.copy(inputAmount = event.amount) }
            is ExpensesUiEvent.OnDescriptionChanged -> _state.update { it.copy(inputDescription = event.description) }
            is ExpensesUiEvent.OnCategorySelected -> _state.update { it.copy(selectedCategory = event.category) }
            is ExpensesUiEvent.OnAddExpenseClicked -> addExpense()
            is ExpensesUiEvent.OnDeleteExpenseClicked -> deleteExpense(event.id)
            is ExpensesUiEvent.OnToggleAddSheet -> _state.update { it.copy(isAddSheetOpened = event.isOpen) }
            is ExpensesUiEvent.OnFilterChanged -> {
                _state.update { it.copy(currentFilter = event.category) }
                applyFilters()
            }
        }
    }

    private fun addExpense() {
        val amount = _state.value.inputAmount.toDoubleOrNull() ?: return
        val description = _state.value.inputDescription.ifBlank { "Без описания" }
        val category = _state.value.selectedCategory.displayName

        _state.update { it.copy(inputAmount = "", inputDescription = "") }
        viewModelScope.launch {
            repository.addExpense(amount, description, category)
        }
    }

    private fun deleteExpense(id: String) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }

    private fun applyFilters() {
        val currentFilter = _state.value.currentFilter

        val filteredList = if (currentFilter == null) {
            allExpenses
        } else {
            allExpenses.filter { it.category == currentFilter.displayName }
        }

        val total = filteredList.sumOf { it.amount }

        _state.update {
            it.copy(
                expenses = filteredList,
                totalSum = total
            )
        }
    }
}
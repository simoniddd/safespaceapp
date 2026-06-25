package com.example.safespace.feature.expenses.presentation.action

sealed interface ExpensesUiEvent {
    data class OnAmountChanged(val amount: String) : ExpensesUiEvent
    data class OnDescriptionChanged(val description: String) : ExpensesUiEvent
    data class OnCategorySelected(val category: String) : ExpensesUiEvent
    data object OnAddExpenseClicked : ExpensesUiEvent
    data class OnDeleteExpenseClicked(val id: String) : ExpensesUiEvent

    data class OnToggleAddSheet(val isOpen: Boolean): ExpensesUiEvent
}
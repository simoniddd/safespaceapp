package com.example.safespace.feature.expenses.presentation.action

import com.example.safespace.core.ui.components.ExpenseCategory

sealed interface ExpensesUiEvent {
    data class OnAmountChanged(val amount: String) : ExpensesUiEvent
    data class OnDescriptionChanged(val description: String) : ExpensesUiEvent
    data class OnCategorySelected(val category: ExpenseCategory) : ExpensesUiEvent
    data object OnAddExpenseClicked : ExpensesUiEvent
    data class OnDeleteExpenseClicked(val id: String) : ExpensesUiEvent

    data class OnToggleAddSheet(val isOpen: Boolean): ExpensesUiEvent
    data class OnFilterChanged(val category: ExpenseCategory?): ExpensesUiEvent
}
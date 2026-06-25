package com.example.safespace.feature.expenses.presentation.state

import com.example.safespace.core.ui.components.ExpenseCategory
import com.example.safespace.feature.expenses.domain.model.ExpensesItem

data class ExpensesState(
    val expenses: List<ExpensesItem> = emptyList(),
    val inputAmount: String = "",
    val inputDescription: String = "",
    val selectedCategory: ExpenseCategory = ExpenseCategory.OTHER,
    val isAddSheetOpened: Boolean = false
)

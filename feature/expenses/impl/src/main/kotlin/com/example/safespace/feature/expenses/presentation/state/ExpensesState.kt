package com.example.safespace.feature.expenses.presentation.state

import com.example.safespace.feature.expenses.domain.model.ExpensesItem

data class ExpensesState(
    val expenses: List<ExpensesItem> = emptyList(),
    val inputAmount: String = "",
    val inputDescription: String = "",
    val selectedCategory: String = "Продукты",
    val categories: List<String> = listOf("Продукты", "Кафе", "Транспорт", "Дом", "Развлечения"),
    val isAddSheetOpened: Boolean = false
)

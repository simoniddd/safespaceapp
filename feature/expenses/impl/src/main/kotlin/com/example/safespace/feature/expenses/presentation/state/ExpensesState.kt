package com.example.safespace.feature.expenses.presentation.state

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.Emotion
import com.example.safespace.core.domain.model.ExpenseCategory
import com.example.safespace.core.domain.model.ExpensesItem

data class ExpensesState(
    val expenses: List<ExpensesItem> = emptyList(),
    val categories: List<ExpenseCategory> = ExpenseCategory.entries,

    val inputAmount: String = "",
    val inputDescription: String = "",
    val selectedCategory: ExpenseCategory = ExpenseCategory.OTHER,
    val isAddSheetOpened: Boolean = false,
    val currentFilter: ExpenseCategory? = null,
    val selectedEmotion: Emotion? = null,

    val totalSum: Double = 0.0,
    val groupedExpenses: Map<String, List<ExpensesItem>> = emptyMap(),
    val categoryTotals: List<Pair<String, Double>> = emptyList(),

    // Слот «📅 Встреча?» в шторке (F-10 ч.1, день 14): события календаря,
    // окно которых накрывает now() на момент открытия шторки. Пусто → чипов нет.
    val matchingEvents: List<CalendarEventItem> = emptyList(),
    val selectedLinkedEvent: CalendarEventItem? = null,
)

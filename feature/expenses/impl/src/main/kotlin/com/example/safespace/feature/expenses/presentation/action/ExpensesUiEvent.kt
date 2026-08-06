package com.example.safespace.feature.expenses.presentation.action

import com.example.safespace.core.domain.model.CalendarEventItem
import com.example.safespace.core.domain.model.Emotion
import com.example.safespace.core.domain.model.ExpenseCategory

sealed interface ExpensesUiEvent {
    data class OnAmountChanged(val amount: String) : ExpensesUiEvent
    data class OnDescriptionChanged(val description: String) : ExpensesUiEvent
    data class OnCategorySelected(val category: ExpenseCategory) : ExpensesUiEvent

    data class OnEmotionSelected(val emotion: Emotion?) : ExpensesUiEvent
    data object OnAddExpenseClicked : ExpensesUiEvent
    data class OnDeleteExpenseClicked(val id: String) : ExpensesUiEvent

    data class OnToggleAddSheet(val isOpen: Boolean) : ExpensesUiEvent
    data class OnFilterChanged(val category: ExpenseCategory?) : ExpensesUiEvent

    // Тап по чипу «📅 Встреча?». null = снять выбор.
    data class OnLinkedEventSelected(val event: CalendarEventItem?) : ExpensesUiEvent
}

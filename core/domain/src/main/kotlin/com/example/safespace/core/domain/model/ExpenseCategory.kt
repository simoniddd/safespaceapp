package com.example.safespace.core.domain.model

enum class ExpenseCategory(
    val displayName: String,
    val emoji: String
) {
    FOOD("Еда", "🍽"),
    SHOPPING("Покупки", "🛒"),
    HOME("Дом", "🏠"),
    SPORT("Спорт", "💪"),
    OTHER("Разное", "📦");

    companion object {
        fun fromName(name: String): ExpenseCategory {
            return entries.find { it.displayName == name } ?: OTHER
        }
    }
}


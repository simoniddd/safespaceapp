package com.example.safespace.core.domain.model

data class ExpensesItem(
    val id: String,
    val amount: Double,
    val description: String,
    val category: String,
    val dateString: String,
    val eventId: String? = null,
    val emotion: Emotion? = null,
    // Снимок serial event id (recurring series) на момент линковки.
    // Храним копию в трате: одна SQL-агрегация по колонке, не JOIN с календарём.
    val recurringEventId: String? = null,
)

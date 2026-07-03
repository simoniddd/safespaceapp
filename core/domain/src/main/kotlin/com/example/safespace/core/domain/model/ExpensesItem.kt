package com.example.safespace.core.domain.model

data class ExpensesItem(
    val id: String,
    val amount: Double,
    val description: String,
    val category: String,
    val dateString: String,
    val eventId: String? = null,
    val emotion: Emotion? = null
)
package com.example.safespace.feature.expenses.domain.repository

import com.example.safespace.core.domain.model.Emotion
import com.example.safespace.core.domain.model.ExpensesItem
import kotlinx.coroutines.flow.Flow

interface ExpensesRepository {
    fun getAllExpenses(): Flow<List<ExpensesItem>>
    suspend fun addExpense(amount: Double, description: String, category: String, emotion: Emotion?)
    suspend fun deleteExpense(id: String)
}
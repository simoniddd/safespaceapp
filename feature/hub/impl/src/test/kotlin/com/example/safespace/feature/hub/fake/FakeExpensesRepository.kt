package com.example.safespace.feature.hub.fake

import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.feature.expenses.domain.repository.ExpensesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeExpensesRepository: ExpensesRepository {

    // контролируем из теста что "лежит в базе"
    private val _expenses = MutableStateFlow<List<ExpensesItem>>(emptyList())

    fun emit(expenses: List<ExpensesItem>) {
        _expenses.value = expenses
    }

    override fun getAllExpenses(): Flow<List<ExpensesItem>> = _expenses

    override suspend fun addExpense(
        amount: Double,
        description: String,
        category: String
    ) = Unit

    override suspend fun deleteExpense(id: String) = Unit

}
package com.example.safespace.feature.expenses.data.repository

import com.example.safespace.core.data.local.dao.ExpensesDao
import com.example.safespace.core.data.local.entity.ExpenseEntity
import com.example.safespace.core.domain.model.ExpensesItem
import com.example.safespace.feature.expenses.domain.repository.ExpensesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

internal class ExpensesRepositoryImpl(
    private val expensesDao: ExpensesDao
) : ExpensesRepository {
    //получаем флоу данных из бд и мапим в доменные модели чтобы использовать дальше
    override fun getAllExpenses(): Flow<List<ExpensesItem>> {
        return expensesDao.getAllExpenses().map { entities ->
            entities.map { entity ->
                ExpensesItem(
                    id = entity.id,
                    amount = entity.amount,
                    description = entity.description,
                    category = entity.category,
                    dateString = entity.dateString,
                    eventId = entity.eventId
                )
            }
        }
    }

    override suspend fun addExpense(
        amount: Double,
        description: String,
        category: String
    ) {
        val entity = ExpenseEntity(
            id = UUID.randomUUID().toString(),
            amount = amount,
            description = description,
            category = category,
            dateString = "Сегодня",
            timestamp = System.currentTimeMillis(),
            eventId = null
        )
        expensesDao.insertExpense(entity)
    }

    override suspend fun deleteExpense(id: String) {
        expensesDao.deleteExpenseById(id)
    }
}
package com.example.safespace.feature.expenses.domain.repository

import com.example.safespace.core.domain.model.Emotion
import com.example.safespace.core.domain.model.ExpensesItem
import kotlinx.coroutines.flow.Flow

interface ExpensesRepository {
    fun getAllExpenses(): Flow<List<ExpensesItem>>

    /**
     * @param eventId id инстанса события календаря, к которому пользователь привязал трату
     *                через чип «📅 Встреча?». Null = не привязано.
     * @param recurringEventId снимок series id (Events._ID) на момент линковки; фиксирует
     *                         принадлежность к серии, даже если пользователь позже удалит
     *                         серию из календаря. Null для one-off событий.
     */
    suspend fun addExpense(
        amount: Double,
        description: String,
        category: String,
        emotion: Emotion?,
        eventId: String? = null,
        recurringEventId: String? = null,
    )

    suspend fun deleteExpense(id: String)

    /**
     * Среднее значение суммы трат за один инстанс recurring-серии.
     * Key = recurringEventId, value = средний total по прошлым инстансам.
     * Одна SQL-агрегация вместо N per-event запросов.
     */
    fun observeRecurringSeriesAverages(): Flow<Map<String, Double>>
}

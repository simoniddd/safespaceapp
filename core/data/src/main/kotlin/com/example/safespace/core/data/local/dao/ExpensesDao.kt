package com.example.safespace.core.data.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.safespace.core.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Row из aggregate-query: одна строка = одна recurring-серия и её среднее
 * значение суммы за один инстанс серии (сгруппировано по eventId внутри).
 */
data class RecurringSeriesAverageRow(
    @ColumnInfo(name = "recurringEventId") val recurringEventId: String,
    @ColumnInfo(name = "avgInstanceTotal") val avgInstanceTotal: Double,
)

@Dao
interface ExpensesDao {

    @Query("SELECT * FROM expenses_table ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses_table WHERE id = :expenseId")
    suspend fun deleteExpenseById(expenseId: String)

    /**
     * Двухуровневая агрегация для «обычно ~$80».
     *
     * Внутренний GROUP BY (recurringEventId, eventId) → сумма расходов за
     *   один конкретный инстанс серии (Monday coffee 2026-08-04: $12+$5=$17).
     * Внешний GROUP BY recurringEventId → среднее по инстансам
     *   («понедельничные кофе обычно $15»).
     *
     * WHERE recurringEventId IS NOT NULL — one-off траты не участвуют.
     */
    @Query(
        """
        SELECT recurringEventId, AVG(instance_total) AS avgInstanceTotal
        FROM (
            SELECT recurringEventId, SUM(amount) AS instance_total
            FROM expenses_table
            WHERE recurringEventId IS NOT NULL
            GROUP BY recurringEventId, eventId
        ) subquery
        GROUP BY recurringEventId
        """
    )
    fun observeRecurringSeriesAverages(): Flow<List<RecurringSeriesAverageRow>>
}

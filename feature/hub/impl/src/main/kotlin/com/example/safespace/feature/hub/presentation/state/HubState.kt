package com.example.safespace.feature.hub.presentation.state

import com.example.safespace.core.domain.model.WeatherIcon
import com.example.safespace.core.domain.model.health.HealthState
import com.example.safespace.feature.hub.presentation.model.TimelineItem

sealed interface PendingQuestion {
    data class IntentionCheck(
        val eventId: String,
        val instanceStart: Long,
        val eventTitle: String,
    ) : PendingQuestion

    data class PendingExpenseCheck(
        val draftId: String,
        val amount: Double,
        val currency: String,
        val merchant: String?,
        val occurredAt: Long
    ) : PendingQuestion
}

/**
 * Свёрнутая сводка события с привязанными тратами:
 *  totalExpense — сумма трат с eventId == этого события ($46)
 *  expenseCount — сколько трат привязано (для «· 3 траты» в свёрнутом виде)
 *  durationHours — длительность события (4ч)
 *  recurringAverage — среднее по прошлым инстансам этой же серии, если событие
 *                     recurring; null для one-off — «обычно ~$80» не показывается.
 */
data class EventAggregate(
    val totalExpense: Double,
    val expenseCount: Int,
    val durationHours: Double,
    val recurringAverage: Double? = null,
)

data class HubState(
    val isLoading: Boolean = true,
    val todayIcon: WeatherIcon? = null,
    val timelineItems: List<TimelineItem> = emptyList(),
    val pendingQuestion: PendingQuestion? = null,
    val healthState: HealthState = HealthState.NotInstalled,
)

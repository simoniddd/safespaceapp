package com.example.safespace.feature.hub.presentation.model

import com.example.safespace.core.domain.state.IntentionState

sealed interface TimelineItem {
    /**
     * Epoch millis для .sortedBy в TimelineMapper.
     *   Expense / Event / HealthDigit(Exercise) — реальное время события.
     *   HealthDigit(Sleep)  — startOfTodayMillis() (верх дня).
     *   HealthDigit(Steps)  — startOfTodayMillis() + 20h (вечерний срез).
     * В UI НЕ рендерится для Health.
     */
    val sortKey: Long

    data class HealthDigit(
        override val sortKey: Long,
        val icon: String, // "😴" / "👟" / "💪"
        val title: String, // "Сон" / "Шаги" / название тренировки
        val digit: String, // "7ч 24м" / "8 042" / "45 мин"
        val source: String? = null, // packageName источника тренировки; null для sleep/steps (агрегаты HC)
    ) : TimelineItem

    data class Expense(
        override val sortKey: Long,
        val time: String,
        val title: String,
        val amount: Long,
    ) : TimelineItem

    data class Event(
        override val sortKey: Long,
        val time: String,
        val eventId: String,
        val instanceStart: Long,
        val durationMin: Int,
        val title: String,
        val subtitle: String,
        val recurringHint: String?,
        val intentionState: IntentionState?,
        val doneCount: Int,
    ) : TimelineItem
}
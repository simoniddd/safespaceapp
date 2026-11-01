package com.example.safespace.feature.hub.presentation.model

/**
 * Единица рендера ленты. Либо одиночный элемент (`Leaf`), либо событие с
 * привязанными тратами внутри (`EventWithExpenses`).
 *
 * Вложение детерминировано (см. TimelineMapper): никаких сводок, счётчиков,
 * «обычно ~$80» — только две группы под шапкой события.
 */
sealed interface TimelineNode {
    val sortKey: Long
    val stableId: String

    data class Leaf(val item: TimelineItem) : TimelineNode {
        override val sortKey: Long = item.sortKey
        override val stableId: String = item.stableId
    }

    data class EventWithExpenses(
        val event: TimelineItem.Event,
        val nested: List<TimelineItem.Expense>,
    ) : TimelineNode {
        override val sortKey: Long = event.sortKey
        override val stableId: String = event.stableId
    }
}

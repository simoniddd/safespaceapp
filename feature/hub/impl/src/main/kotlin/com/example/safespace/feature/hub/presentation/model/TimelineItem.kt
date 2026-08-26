package com.example.safespace.feature.hub.presentation.model

import kotlinx.datetime.LocalTime

sealed interface TimelineItem {
    val time: LocalTime

    data class HealthDigit(
        override val time: LocalTime,
        val icon: String,
        val title: String,
        val digit: String,
    ): TimelineItem
}
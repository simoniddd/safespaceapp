package com.example.safespace.core.domain.model.health

import kotlinx.datetime.LocalDate

data class SleepSummary(
    val durationMinutes: Int,
    val date: LocalDate
)
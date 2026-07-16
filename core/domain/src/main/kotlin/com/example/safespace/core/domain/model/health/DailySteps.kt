package com.example.safespace.core.domain.model.health

import kotlinx.datetime.LocalDate

data class DailySteps(
    val count: Int,
    val date: LocalDate
)
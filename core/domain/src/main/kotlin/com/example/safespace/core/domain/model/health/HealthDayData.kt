package com.example.safespace.core.domain.model.health

data class HealthDayData(
    val steps: Int?,
    val sleep: SleepSummary?,
    val exercises: List<ExerciseSession>,
)
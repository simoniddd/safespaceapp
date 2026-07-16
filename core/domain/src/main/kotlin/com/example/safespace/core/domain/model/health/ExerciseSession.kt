package com.example.safespace.core.domain.model.health

import kotlinx.datetime.LocalDateTime

data class ExerciseSession(
    val durationMinutes: Int,
    val title: String,
    val source: String,
    val startTime: LocalDateTime
)
package com.example.safespace.core.domain.repository

import com.example.safespace.core.domain.model.health.DailySteps
import com.example.safespace.core.domain.model.health.ExerciseSession
import com.example.safespace.core.domain.model.health.SleepSummary
import kotlinx.datetime.LocalDate

interface HealthRepository {
    suspend fun getStepsForDate(date: LocalDate): DailySteps?
    suspend fun getSleepForDate(date: LocalDate): SleepSummary?
    suspend fun getExercisesForDate(date: LocalDate): List<ExerciseSession>
    suspend fun isAvailable(): HealthAvailability

    fun permissionsToRequest(): Set<String>
}

enum class HealthAvailability {
    AVAILABLE, NOT_INSTALLED, NO_PERMISSION
}

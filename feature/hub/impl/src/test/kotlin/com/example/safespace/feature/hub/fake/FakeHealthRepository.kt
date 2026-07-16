package com.example.safespace.feature.hub.fake

import com.example.safespace.core.domain.model.health.DailySteps
import com.example.safespace.core.domain.model.health.ExerciseSession
import com.example.safespace.core.domain.model.health.SleepSummary
import com.example.safespace.core.domain.repository.HealthAvailability
import com.example.safespace.core.domain.repository.HealthRepository
import kotlinx.datetime.LocalDate

class FakeHealthRepository : HealthRepository {
    override suspend fun getStepsForDate(date: LocalDate): DailySteps? = null
    override suspend fun getSleepForDate(date: LocalDate): SleepSummary? = null
    override suspend fun getExercisesForDate(date: LocalDate): List<ExerciseSession> = emptyList()
    override suspend fun isAvailable(): HealthAvailability = HealthAvailability.NOT_INSTALLED
}

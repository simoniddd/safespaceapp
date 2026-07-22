package com.example.safespace.core.domain.repository

import com.example.safespace.core.domain.model.health.HealthDataType
import com.example.safespace.core.domain.model.health.HealthState
import kotlinx.datetime.LocalDate

interface HealthRepository {
    suspend fun getState(date: LocalDate): HealthState

    fun permissionsFor(types: Set<HealthDataType>): Set<String>
}

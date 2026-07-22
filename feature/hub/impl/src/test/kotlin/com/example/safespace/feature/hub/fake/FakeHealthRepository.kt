package com.example.safespace.feature.hub.fake

import com.example.safespace.core.domain.model.health.HealthDataType
import com.example.safespace.core.domain.model.health.HealthState
import com.example.safespace.core.domain.repository.HealthRepository
import kotlinx.datetime.LocalDate

class FakeHealthRepository(
    private val stateProvider: (LocalDate) -> HealthState = { HealthState.NotInstalled },
) : HealthRepository {
    override suspend fun getState(date: LocalDate): HealthState = stateProvider(date)

    override fun permissionsFor(types: Set<HealthDataType>): Set<String> =
        types.map { "fake_permission_${it.name}" }.toSet()
}

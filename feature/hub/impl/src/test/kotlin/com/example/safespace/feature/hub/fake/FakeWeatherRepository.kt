package com.example.safespace.feature.hub.fake

import com.example.safespace.core.domain.model.WeatherIcon
import com.example.safespace.core.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate

class FakeWeatherRepository : WeatherRepository {
    override suspend fun saveWeather(date: LocalDate, icon: WeatherIcon) = Unit
    override fun observeToday(): Flow<WeatherIcon?> = flowOf(null)
}

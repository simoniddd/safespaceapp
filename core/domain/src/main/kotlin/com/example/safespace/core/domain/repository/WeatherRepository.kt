package com.example.safespace.core.domain.repository

import com.example.safespace.core.domain.model.WeatherIcon
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface WeatherRepository {
    suspend fun saveWeather(date: LocalDate, icon: WeatherIcon)
    fun observeToday(): Flow<WeatherIcon?>
}
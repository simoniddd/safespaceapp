package com.example.safespace.core.data.repository

import com.example.safespace.core.data.local.dao.WeatherDao
import com.example.safespace.core.data.local.entity.DayWeatherEntity
import com.example.safespace.core.domain.model.WeatherIcon
import com.example.safespace.core.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class WeatherRepositoryImpl(
    private val weatherDao: WeatherDao
) : WeatherRepository {
    override suspend fun saveWeather(date: LocalDate, icon: WeatherIcon) {
        val entity = DayWeatherEntity(
            dateString = date.toString(),
            iconName = icon.name
        )
        weatherDao.upsert(entity)
    }

    @OptIn(ExperimentalTime::class)
    override fun observeToday(): Flow<WeatherIcon?> {
        val date = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        return weatherDao.observe(date).map { entity ->
            entity?.let { WeatherIcon.valueOf(it.iconName) }
        }
    }
}
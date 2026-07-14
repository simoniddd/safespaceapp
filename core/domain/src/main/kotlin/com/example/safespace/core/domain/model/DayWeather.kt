package com.example.safespace.core.domain.model

import kotlinx.datetime.LocalDate


data class DayWeather(
    val date: LocalDate,
    val icon: WeatherIcon
)


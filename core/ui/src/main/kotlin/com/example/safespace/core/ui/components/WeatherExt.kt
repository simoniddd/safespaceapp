package com.example.safespace.core.ui.components

import com.example.safespace.core.domain.model.WeatherIcon

/** 5 состояний погоды. Порядок = порядок в веере. emoji — «природный» цвет сам. */
val WeatherIcon.emoji: String get() = when (this) {
    WeatherIcon.SUNNY  -> "☀️"
    WeatherIcon.PARTLY -> "🌤"
    WeatherIcon.CLOUDY -> "☁️"
    WeatherIcon.RAIN   -> "🌧"
    WeatherIcon.STORM  -> "⛈️"
}
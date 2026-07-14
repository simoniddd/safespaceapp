package com.example.safespace.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_weather")
data class DayWeatherEntity(
    @PrimaryKey
    val dateString: String,
    val iconName: String
)
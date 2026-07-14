package com.example.safespace.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.safespace.core.data.local.entity.DayWeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Upsert suspend fun upsert(entity: DayWeatherEntity)

    @Query("SELECT * FROM day_weather WHERE dateString =:date")
    fun observe(date: String): Flow<DayWeatherEntity?>

}
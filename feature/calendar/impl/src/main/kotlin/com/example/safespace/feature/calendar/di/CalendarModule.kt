package com.example.safespace.feature.calendar.di

import com.example.safespace.feature.calendar.data.repository.CalendarRepositoryImpl
import com.example.safespace.feature.calendar.domain.repository.CalendarRepository
import com.example.safespace.feature.calendar.navigation.CalendarEntryPoint
import com.example.safespace.feature.calendar.presentation.navigation.CalendarEntryPointImpl
import org.koin.dsl.module

val calendarModule = module {
    single<CalendarEntryPoint> { CalendarEntryPointImpl() }
    single<CalendarRepository> { CalendarRepositoryImpl(get()) }
}

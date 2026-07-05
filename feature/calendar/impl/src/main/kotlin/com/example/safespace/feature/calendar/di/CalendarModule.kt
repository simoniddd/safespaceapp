package com.example.safespace.feature.calendar.di

import com.example.safespace.core.domain.source.EventsSource
import com.example.safespace.feature.calendar.data.repository.CalendarRepositoryImpl
import com.example.safespace.feature.calendar.data.source.SystemCalendarSource
import com.example.safespace.feature.calendar.domain.repository.CalendarRepository
import com.example.safespace.feature.calendar.navigation.CalendarEntryPoint
import com.example.safespace.feature.calendar.presentation.navigation.CalendarEntryPointImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val calendarModule = module {
    single<CalendarEntryPoint> { CalendarEntryPointImpl() }
    single<CalendarRepository> { CalendarRepositoryImpl(get()) }
    single<EventsSource> { SystemCalendarSource(androidContext()) }
}

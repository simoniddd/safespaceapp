package com.example.safespace.feature.calendar.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.example.safespace.core.domain.source.EventsSource
import com.example.safespace.feature.calendar.data.repository.CalendarRepositoryImpl
import com.example.safespace.feature.calendar.data.source.CalendarPreferencesDataStore
import com.example.safespace.feature.calendar.data.source.SystemCalendarSource
import com.example.safespace.feature.calendar.data.source.SystemCalendarsSource
import com.example.safespace.feature.calendar.domain.repository.CalendarRepository
import com.example.safespace.feature.calendar.domain.source.CalendarSource
import com.example.safespace.feature.calendar.navigation.CalendarEntryPoint
import com.example.safespace.feature.calendar.presentation.navigation.CalendarEntryPointImpl
import com.example.safespace.feature.calendar.presentation.viwemodel.CalendarSettingViewmodel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val calendarModule = module {
    single<CalendarEntryPoint> { CalendarEntryPointImpl() }
    single<CalendarRepository> { CalendarRepositoryImpl(get()) }
    single<EventsSource> { SystemCalendarSource(androidContext()) }
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            produceFile = { androidContext().filesDir.resolve("calendar_prefs.preferences_pb") }
        )
    }
    single<CalendarSource> { get<CalendarPreferencesDataStore>() }
    single { CalendarPreferencesDataStore(get()) }
    single<SystemCalendarsSource> { get() }
    viewModel { CalendarSettingViewmodel(get(), get(), get()) }
}

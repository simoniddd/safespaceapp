package com.example.safespace.feature.hub.di

import com.example.safespace.feature.hub.navigation.HubEntryPoint
import com.example.safespace.feature.hub.presentation.navigation.HubEntryPointImpl
import com.example.safespace.feature.hub.presentation.viewmodel.HubViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val hubModule = module {
    single<HubEntryPoint> { HubEntryPointImpl() }
    viewModel { HubViewModel(
        get(),
        get(),
        get(),
        eventsSource = get(),
        calendarSource = get(),
        navigator = get()
    ) }
}

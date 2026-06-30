package com.example.safespace.core.presentation.di

import com.example.safespace.core.presentation.navigation.Navigator
import com.example.safespace.core.presentation.navigation.NavigatorImpl
import org.koin.dsl.module

val presentationModule = module {
    single<Navigator> { NavigatorImpl() }
}

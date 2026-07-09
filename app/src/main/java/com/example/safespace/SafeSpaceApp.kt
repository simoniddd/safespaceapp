package com.example.safespace

import android.app.Application
import com.example.safespace.core.data.di.dataModule
import com.example.safespace.core.di.appModule
import com.example.safespace.core.presentation.di.presentationModule
import com.example.safespace.feature.calendar.di.calendarModule
import com.example.safespace.feature.expenses.di.expensesModule
import com.example.safespace.feature.hub.di.hubModule

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
class SafeSpaceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SafeSpaceApp)
            workManagerFactory()
            modules(
                presentationModule,
                appModule,
                dataModule,
                expensesModule,
                hubModule,
                calendarModule
            )
        }
    }
}

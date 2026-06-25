package com.example.safespace

import android.app.Application
import com.example.safespace.core.di.appModule
import com.example.safespace.feature.expenses.di.expensesModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SafeSpaceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SafeSpaceApp)
            modules(
                appModule,
                expensesModule,
            )
        }
    }
}
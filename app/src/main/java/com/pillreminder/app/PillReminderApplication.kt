package com.pillreminder.app

import android.app.Application
import com.pillreminder.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PillReminderApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin for dependency injection
        startKoin {
            androidContext(this@PillReminderApplication)
            modules(appModule)
        }
    }
}
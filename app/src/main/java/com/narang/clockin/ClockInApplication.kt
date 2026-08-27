package com.narang.clockin

import android.app.Application
import timber.log.Timber

class ClockInApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}

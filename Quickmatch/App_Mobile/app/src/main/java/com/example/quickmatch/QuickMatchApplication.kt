package com.example.quickmatch

import android.app.Application
import timber.log.Timber

class QuickMatchApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
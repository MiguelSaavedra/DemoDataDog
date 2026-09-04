package com.example.demodatadog

import android.app.Application
import com.example.demodatadog.monitoring.DatadogTracker

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DatadogTracker.initialize(this)
    }
}

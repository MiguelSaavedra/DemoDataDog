package com.example.demodatadog.monitoring

import android.app.Activity
import android.app.Application
import android.util.Log
import com.datadog.android.Datadog
import com.datadog.android.DatadogSite
import com.datadog.android.compose.enableComposeActionTracking
import com.datadog.android.core.configuration.Configuration
import com.datadog.android.log.Logger
import com.datadog.android.log.Logs
import com.datadog.android.log.LogsConfiguration
import com.datadog.android.privacy.TrackingConsent
import com.datadog.android.rum.GlobalRumMonitor
import com.datadog.android.rum.Rum
import com.datadog.android.rum.RumActionType
import com.datadog.android.rum.RumConfiguration
import com.datadog.android.rum.RumErrorSource
import com.datadog.android.rum.tracking.ActivityViewTrackingStrategy
import com.datadog.android.rum.tracking.ComponentPredicate
import com.datadog.android.trace.Trace
import com.datadog.android.trace.TraceConfiguration
import com.example.demodatadog.BuildConfig
import com.example.demodatadog.MainActivity
import java.util.concurrent.atomic.AtomicBoolean

object DatadogTracker {

    private const val TAG = "DatadogTracker"
    private const val SERVICE_NAME = "demo-datadog"
    private val initialized = AtomicBoolean(false)
    private var logger: Logger? = null

    fun initialize(application: Application) {
        if (!initialized.compareAndSet(false, true)) return

        val clientToken = BuildConfig.DATADOG_CLIENT_TOKEN
        val applicationId = BuildConfig.DATADOG_APPLICATION_ID
        if (clientToken.isBlank() || applicationId.isBlank()) {
            initialized.set(false)
            return
        }

        val configuration = Configuration.Builder(
            clientToken = clientToken,
            env = BuildConfig.DATADOG_ENV,
            variant = "",
            service = SERVICE_NAME,
        )
            .useSite(DatadogSite.US5)
            .setFirstPartyHosts(firstPartyHosts())
            .build()

        Datadog.initialize(application, configuration, TrackingConsent.GRANTED)
        Logs.enable(LogsConfiguration.Builder().build())
        Trace.enable(TraceConfiguration.Builder().build())

        Rum.enable(
            RumConfiguration.Builder(applicationId)
                .trackUserInteractions()
                .trackLongTasks()
                .useViewTrackingStrategy(
                    ActivityViewTrackingStrategy(
                        trackExtras = false,
                        componentPredicate = object : ComponentPredicate<Activity> {
                            override fun accept(component: Activity): Boolean = component !is MainActivity
                            override fun getViewName(component: Activity): String? = null
                        },
                    )
                )
                .enableComposeActionTracking()
                .build()
        )

        logger = Logger.Builder()
            .setName(SERVICE_NAME)
            .setBundleWithRumEnabled(true)
            .build()

        track(DatadogEvent.APP_LAUNCH)
    }

    fun track(event: String, attributes: Map<String, Any?> = emptyMap()) {
        if (!initialized.get()) return
        GlobalRumMonitor.get().addAction(RumActionType.CUSTOM, event, attributes)
    }

    fun logError(message: String, throwable: Throwable? = null) {
        logger?.e(message, throwable)
        if (initialized.get()) {
            GlobalRumMonitor.get().addError(message, RumErrorSource.SOURCE, throwable, emptyMap())
        }
    }

    fun firstPartyHosts(): List<String> = listOf("pokeapi.co")
}

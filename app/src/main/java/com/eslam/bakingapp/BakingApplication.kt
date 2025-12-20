package com.eslam.bakingapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.eslam.metrics.api.MetricsConfig
import com.eslam.metrics.api.MetricsSDK
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class for BakingApp.
 * Initializes Hilt dependency injection and MetricsSDK.
 */
@HiltAndroidApp
class BakingApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    override fun onCreate() {
        super.onCreate()
        initializeMetricsSDK()
    }
    
    /**
     * Initialize the MetricsSDK for session recording and performance monitoring.
     */
    private fun initializeMetricsSDK() {
        val config = MetricsConfig.builder()
            .enableAnrDetection(true)
            .enableMemoryMonitoring(true)
            .enableCrashReporting(true)
            .enableScreenshots(true)
            .screenshotQuality(40)
            .gracePeriodMs(5000)
            .dataRetentionDays(7)
            .debugLogging(BuildConfig.DEBUG)
            .build()
        
        MetricsSDK.init(this, config)
    }
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}




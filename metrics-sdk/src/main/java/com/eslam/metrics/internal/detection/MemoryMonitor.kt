package com.eslam.metrics.internal.detection

import android.app.ActivityManager
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import com.eslam.metrics.internal.bridge.NativeBridge

/**
 * MemoryMonitor - Monitors memory usage and detects spikes
 *
 * Uses ComponentCallbacks2.onTrimMemory for low memory detection.
 * Periodically samples memory usage.
 */
internal class MemoryMonitor(
    private val context: Context,
    private val nativeBridge: NativeBridge,
    private val onMemorySpike: () -> Unit,
    private val onLowMemory: () -> Unit
) : ComponentCallbacks2 {

    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private var samplingIntervalMs = 5000L // 5 seconds

    private val samplingRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                sampleMemory()
                handler.postDelayed(this, samplingIntervalMs)
            }
        }
    }

    fun start() {
        if (!isRunning) {
            isRunning = true
            context.registerComponentCallbacks(this)
            handler.post(samplingRunnable)
        }
    }

    fun stop() {
        isRunning = false
        context.unregisterComponentCallbacks(this)
        handler.removeCallbacks(samplingRunnable)
    }

    fun setSamplingInterval(intervalMs: Long) {
        samplingIntervalMs = intervalMs
    }

    private fun sampleMemory() {
        try {
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)

            val totalMb = memoryInfo.totalMem / (1024 * 1024)
            val availableMb = memoryInfo.availMem / (1024 * 1024)
            val usedMb = totalMb - availableMb

            nativeBridge.nativeRecordMemoryMetrics(totalMb, usedMb, availableMb)

            // Check for spike
            if (nativeBridge.nativeIsMemorySpike()) {
                onMemorySpike()
            }
        } catch (e: Exception) {
            // Ignore sampling errors
        }
    }

    // ComponentCallbacks2 implementation

    override fun onTrimMemory(level: Int) {
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> {
                onLowMemory()
            }
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                onLowMemory()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // Not used
    }

    override fun onLowMemory() {
        onLowMemory()
    }
}

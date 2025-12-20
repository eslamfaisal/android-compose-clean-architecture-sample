package com.eslam.metrics.internal.detection

import android.os.Handler
import android.os.Looper
import com.eslam.metrics.internal.bridge.NativeBridge

/**
 * AnrWatchdog - Detects Application Not Responding conditions
 *
 * Works in conjunction with native watchdog.
 * Pings the native layer from the main thread.
 */
internal class AnrWatchdog(
    private val nativeBridge: NativeBridge,
    private val pingIntervalMs: Long = 1000
) {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var isRunning = false

    private val pingRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                // Ping native watchdog from main thread
                nativeBridge.nativePingWatchdog()
                mainHandler.postDelayed(this, pingIntervalMs)
            }
        }
    }

    fun start() {
        if (!isRunning) {
            isRunning = true
            // Start native watchdog
            nativeBridge.nativeStartWatchdog()
            // Start pinging from main thread
            mainHandler.post(pingRunnable)
        }
    }

    fun stop() {
        isRunning = false
        mainHandler.removeCallbacks(pingRunnable)
        nativeBridge.nativeStopWatchdog()
    }
}

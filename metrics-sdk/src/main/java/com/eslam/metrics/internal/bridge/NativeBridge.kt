package com.eslam.metrics.internal.bridge

import android.graphics.Bitmap

/**
 * NativeBridge - JNI wrapper for native C++ code
 *
 * This class provides the bridge between Kotlin and the native C++ layer.
 * All heavy operations are performed in C++ for zero UI lag.
 */
internal class NativeBridge {

    companion object {
        init {
            System.loadLibrary("metrics_sdk")
        }
    }

    // Initialization
    external fun nativeInit(storagePath: String)
    external fun nativeSetEventCallback(callback: NativeEventCallback)

    // Session Management
    external fun nativeStartSession(): String
    external fun nativeEndSession()
    external fun nativePauseSession()
    external fun nativeResumeSession()
    external fun nativeGetSessionId(): String
    external fun nativeGetSessionDuration(): Long
    external fun nativeSetUserInfo(userId: String, email: String?)

    // Metrics
    external fun nativeRecordMemoryMetrics(totalMb: Long, usedMb: Long, availableMb: Long)
    external fun nativeRecordCpuMetrics(usagePercentage: Float, coreCount: Int)
    external fun nativeIsMemorySpike(): Boolean

    // Events
    external fun nativeStartWatchdog()
    external fun nativeStopWatchdog()
    external fun nativePingWatchdog()
    external fun nativeRecordEvent(eventType: Int, name: String, metadata: String)
    external fun nativeRecordHeavyAction(name: String, metadata: String)
    external fun nativeRecordCrash(stackTrace: String)

    // Image Processing
    external fun nativeProcessBitmap(bitmap: Bitmap, outputPath: String): String?
    external fun nativeSetImageConfig(targetWidth: Int, targetHeight: Int, quality: Int, useWebP: Boolean)
    external fun nativeIsLowMemory(): Boolean

    // Cleanup
    external fun nativeReset()
}

/**
 * Callback interface for native events
 */
internal interface NativeEventCallback {
    fun onEvent(eventType: Int, name: String, metadata: String, timestampMs: Long)
}

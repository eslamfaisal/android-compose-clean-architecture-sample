package com.eslam.metrics.api

/**
 * MetricsConfig - Configuration options for the SDK
 */
data class MetricsConfig(
    /**
     * Grace period in milliseconds before session ends when app goes to background.
     * Default: 5000ms (5 seconds)
     */
    val gracePeriodMs: Long = 5000,

    /**
     * Enable ANR detection.
     * Default: true
     */
    val enableAnrDetection: Boolean = true,

    /**
     * Enable memory spike detection.
     * Default: true
     */
    val enableMemoryMonitoring: Boolean = true,

    /**
     * Enable crash reporting.
     * Default: true
     */
    val enableCrashReporting: Boolean = true,

    /**
     * Enable screenshot capture on events.
     * Default: true
     */
    val enableScreenshots: Boolean = true,

    /**
     * Memory usage threshold percentage for spike detection.
     * Default: 80.0
     */
    val memoryThresholdPercent: Float = 80.0f,

    /**
     * Screenshot quality (0-100).
     * Default: 40
     */
    val screenshotQuality: Int = 40,

    /**
     * Target screenshot width in pixels.
     * Default: 360
     */
    val screenshotWidth: Int = 360,

    /**
     * Target screenshot height in pixels.
     * Default: 640
     */
    val screenshotHeight: Int = 640,

    /**
     * Maximum age of stored data in days.
     * Default: 7
     */
    val dataRetentionDays: Int = 7,

    /**
     * Enable debug logging.
     * Default: false
     */
    val debugLogging: Boolean = false
) {
    class Builder {
        private var gracePeriodMs: Long = 5000
        private var enableAnrDetection: Boolean = true
        private var enableMemoryMonitoring: Boolean = true
        private var enableCrashReporting: Boolean = true
        private var enableScreenshots: Boolean = true
        private var memoryThresholdPercent: Float = 80.0f
        private var screenshotQuality: Int = 40
        private var screenshotWidth: Int = 360
        private var screenshotHeight: Int = 640
        private var dataRetentionDays: Int = 7
        private var debugLogging: Boolean = false

        fun gracePeriodMs(value: Long) = apply { gracePeriodMs = value }
        fun enableAnrDetection(value: Boolean) = apply { enableAnrDetection = value }
        fun enableMemoryMonitoring(value: Boolean) = apply { enableMemoryMonitoring = value }
        fun enableCrashReporting(value: Boolean) = apply { enableCrashReporting = value }
        fun enableScreenshots(value: Boolean) = apply { enableScreenshots = value }
        fun memoryThresholdPercent(value: Float) = apply { memoryThresholdPercent = value }
        fun screenshotQuality(value: Int) = apply { screenshotQuality = value.coerceIn(0, 100) }
        fun screenshotWidth(value: Int) = apply { screenshotWidth = value }
        fun screenshotHeight(value: Int) = apply { screenshotHeight = value }
        fun dataRetentionDays(value: Int) = apply { dataRetentionDays = value }
        fun debugLogging(value: Boolean) = apply { debugLogging = value }

        fun build() = MetricsConfig(
            gracePeriodMs = gracePeriodMs,
            enableAnrDetection = enableAnrDetection,
            enableMemoryMonitoring = enableMemoryMonitoring,
            enableCrashReporting = enableCrashReporting,
            enableScreenshots = enableScreenshots,
            memoryThresholdPercent = memoryThresholdPercent,
            screenshotQuality = screenshotQuality,
            screenshotWidth = screenshotWidth,
            screenshotHeight = screenshotHeight,
            dataRetentionDays = dataRetentionDays,
            debugLogging = debugLogging
        )
    }

    companion object {
        fun builder() = Builder()
    }
}

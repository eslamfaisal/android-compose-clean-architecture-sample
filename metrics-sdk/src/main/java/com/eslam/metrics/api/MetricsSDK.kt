package com.eslam.metrics.api

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.eslam.metrics.data.repository.MetricsRepository
import com.eslam.metrics.internal.bridge.EventType
import com.eslam.metrics.internal.bridge.NativeBridge
import com.eslam.metrics.internal.bridge.NativeEventCallback
import com.eslam.metrics.internal.capture.ScreenshotManager
import com.eslam.metrics.internal.detection.AnrWatchdog
import com.eslam.metrics.internal.detection.CrashHandler
import com.eslam.metrics.internal.detection.MemoryMonitor
import com.eslam.metrics.internal.lifecycle.LifecycleTracker
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

/**
 * MetricsSDK - Public API for Session Recording & Metrics
 *
 * A high-performance, lightweight SDK for capturing user sessions,
 * performance bottlenecks, and visual context without blocking the UI thread.
 *
 * Usage:
 * ```
 * // Initialize in Application.onCreate()
 * MetricsSDK.init(this)
 *
 * // Set user info after login
 * MetricsSDK.setUserInfo("user123", "user@example.com")
 *
 * // Track custom actions
 * MetricsSDK.trackAction("checkout_clicked")
 * MetricsSDK.trackHeavyAction("payment_processed", mapOf("amount" to 99.99))
 * ```
 */
object MetricsSDK {

    private const val TAG = "MetricsSDK"
    private const val SCREENSHOTS_DIR = "metrics_screenshots"

    private var isInitialized = false
    private var config = MetricsConfig()

    private lateinit var application: Application
    private lateinit var nativeBridge: NativeBridge
    private lateinit var repository: MetricsRepository
    private lateinit var lifecycleTracker: LifecycleTracker
    private lateinit var screenshotManager: ScreenshotManager
    private lateinit var memoryMonitor: MemoryMonitor
    private lateinit var anrWatchdog: AnrWatchdog
    private lateinit var crashHandler: CrashHandler

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val mapAdapter = moshi.adapter<Map<String, Any>>(Map::class.java)

    private var currentSessionId: String? = null
    private var userId: String? = null
    private var userEmail: String? = null

    /**
     * Initialize the SDK with default configuration.
     *
     * @param context Application context
     */
    @JvmStatic
    fun init(context: Context) {
        init(context, MetricsConfig())
    }

    /**
     * Initialize the SDK with custom configuration.
     *
     * @param context Application context
     * @param config SDK configuration
     */
    @JvmStatic
    fun init(context: Context, config: MetricsConfig) {
        if (isInitialized) {
            log("SDK already initialized")
            return
        }

        this.application = context.applicationContext as Application
        this.config = config

        try {
            initializeComponents()
            isInitialized = true
            log("SDK initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize SDK", e)
        }
    }

    private fun initializeComponents() {
        // Initialize native bridge
        nativeBridge = NativeBridge()
        val storageDir = File(application.filesDir, SCREENSHOTS_DIR).apply { mkdirs() }
        nativeBridge.nativeInit(storageDir.absolutePath)

        // Set native config
        nativeBridge.nativeSetImageConfig(
            config.screenshotWidth,
            config.screenshotHeight,
            config.screenshotQuality,
            false // Use JPEG
        )

        // Set event callback
        nativeBridge.nativeSetEventCallback(object : NativeEventCallback {
            override fun onEvent(eventType: Int, name: String, metadata: String, timestampMs: Long) {
                handleNativeEvent(EventType.fromValue(eventType), name, metadata)
            }
        })

        // Initialize repository
        repository = MetricsRepository(application)

        // Initialize screenshot manager
        screenshotManager = ScreenshotManager(nativeBridge, storageDir)

        // Initialize lifecycle tracker
        lifecycleTracker = LifecycleTracker(
            application = application,
            onForeground = ::onAppForeground,
            onBackground = ::onAppBackground,
            onActivityResumed = ::onActivityResumed,
            gracePeriodMs = config.gracePeriodMs
        )
        lifecycleTracker.start()

        // Initialize memory monitor
        if (config.enableMemoryMonitoring) {
            memoryMonitor = MemoryMonitor(
                context = application,
                nativeBridge = nativeBridge,
                onMemorySpike = ::onMemorySpike,
                onLowMemory = ::onLowMemory
            )
            memoryMonitor.start()
        }

        // Initialize ANR watchdog
        if (config.enableAnrDetection) {
            anrWatchdog = AnrWatchdog(nativeBridge)
            anrWatchdog.start()
        }

        // Initialize crash handler
        if (config.enableCrashReporting) {
            crashHandler = CrashHandler(nativeBridge) { stackTrace ->
                onCrashCaptured(stackTrace)
            }
            crashHandler.install()
        }

        // Cleanup old data
        repository.cleanupOldData(config.dataRetentionDays)
    }

    /**
     * Set user identification info.
     *
     * @param userId Unique user identifier
     * @param email Optional user email
     */
    @JvmStatic
    fun setUserInfo(userId: String, email: String? = null) {
        ensureInitialized()
        this.userId = userId
        this.userEmail = email
        nativeBridge.nativeSetUserInfo(userId, email)
        log("User info set: $userId")
    }

    /**
     * Track a simple action/event.
     *
     * @param name Action name
     */
    @JvmStatic
    fun trackAction(name: String) {
        ensureInitialized()
        val sessionId = currentSessionId ?: return
        
        nativeBridge.nativeRecordEvent(
            EventType.CUSTOM.value,
            name,
            "{}"
        )
        
        repository.recordEvent(
            sessionId = sessionId,
            eventType = EventType.CUSTOM,
            eventName = name,
            metadata = null,
            screenshotPath = null,
            memoryUsageMb = null,
            cpuUsagePercent = null
        )
        
        log("Action tracked: $name")
    }

    /**
     * Track a heavy action with metadata and screenshot capture.
     *
     * @param name Action name
     * @param meta Additional metadata
     */
    @JvmStatic
    fun trackHeavyAction(name: String, meta: Map<String, Any> = emptyMap()) {
        ensureInitialized()
        val sessionId = currentSessionId ?: return
        
        val metadataJson = try {
            mapAdapter.toJson(meta)
        } catch (e: Exception) {
            "{}"
        }

        nativeBridge.nativeRecordHeavyAction(name, metadataJson)

        // Capture screenshot if enabled
        if (config.enableScreenshots) {
            captureScreenshotForEvent(name, sessionId, metadataJson)
        } else {
            repository.recordEvent(
                sessionId = sessionId,
                eventType = EventType.HEAVY_ACTION,
                eventName = name,
                metadata = metadataJson,
                screenshotPath = null,
                memoryUsageMb = null,
                cpuUsagePercent = null
            )
        }

        log("Heavy action tracked: $name")
    }

    /**
     * Get the current session ID.
     *
     * @return Current session ID or null if no active session
     */
    @JvmStatic
    fun getSessionId(): String? {
        if (!isInitialized) return null
        return currentSessionId
    }

    /**
     * Check if the SDK is initialized.
     *
     * @return true if initialized
     */
    @JvmStatic
    fun isInitialized(): Boolean = isInitialized

    // Internal methods

    private fun onAppForeground() {
        log("App entered foreground")
        val sessionId = nativeBridge.nativeStartSession()
        currentSessionId = sessionId
        
        repository.createSession(
            sessionId = sessionId,
            userId = userId,
            userEmail = userEmail
        )
    }

    private fun onAppBackground() {
        log("App entered background")
        val sessionId = currentSessionId ?: return
        val duration = nativeBridge.nativeGetSessionDuration()
        
        nativeBridge.nativeEndSession()
        repository.endSession(sessionId, duration)
        
        currentSessionId = null
    }

    private fun onActivityResumed(activity: Activity) {
        log("Activity resumed: ${activity.javaClass.simpleName}")
    }

    private fun onMemorySpike() {
        log("Memory spike detected")
        val sessionId = currentSessionId ?: return

        if (config.enableScreenshots) {
            captureScreenshotForEvent("memory_spike", sessionId, null)
        } else {
            repository.recordEvent(
                sessionId = sessionId,
                eventType = EventType.MEMORY_SPIKE,
                eventName = "memory_spike",
                metadata = null,
                screenshotPath = null,
                memoryUsageMb = null,
                cpuUsagePercent = null
            )
        }
    }

    private fun onLowMemory() {
        log("Low memory state")
        screenshotManager.setLowMemoryState(true)
    }

    private fun onCrashCaptured(stackTrace: String) {
        log("Crash captured")
        val sessionId = currentSessionId ?: return

        repository.recordEvent(
            sessionId = sessionId,
            eventType = EventType.CRASH,
            eventName = "app_crash",
            metadata = stackTrace,
            screenshotPath = null,
            memoryUsageMb = null,
            cpuUsagePercent = null
        )
    }

    private fun handleNativeEvent(eventType: EventType, name: String, metadata: String) {
        val sessionId = currentSessionId ?: return
        
        when (eventType) {
            EventType.ANR -> {
                log("ANR detected from native")
                if (config.enableScreenshots) {
                    captureScreenshotForEvent("anr", sessionId, metadata)
                } else {
                    repository.recordEvent(
                        sessionId = sessionId,
                        eventType = eventType,
                        eventName = name,
                        metadata = metadata,
                        screenshotPath = null,
                        memoryUsageMb = null,
                        cpuUsagePercent = null
                    )
                }
            }
            else -> {
                repository.recordEvent(
                    sessionId = sessionId,
                    eventType = eventType,
                    eventName = name,
                    metadata = metadata,
                    screenshotPath = null,
                    memoryUsageMb = null,
                    cpuUsagePercent = null
                )
            }
        }
    }

    private fun captureScreenshotForEvent(
        eventName: String,
        sessionId: String,
        metadata: String?
    ) {
        val activity = lifecycleTracker.getCurrentActivity() ?: return

        screenshotManager.captureScreenshot(
            activity = activity,
            eventName = eventName,
            callback = object : ScreenshotManager.ScreenshotCallback {
                override fun onSuccess(filePath: String) {
                    log("Screenshot captured: $filePath")
                    repository.recordEvent(
                        sessionId = sessionId,
                        eventType = EventType.HEAVY_ACTION,
                        eventName = eventName,
                        metadata = metadata,
                        screenshotPath = filePath,
                        memoryUsageMb = null,
                        cpuUsagePercent = null
                    )
                }

                override fun onFailure(reason: String) {
                    log("Screenshot failed: $reason")
                    repository.recordEvent(
                        sessionId = sessionId,
                        eventType = EventType.HEAVY_ACTION,
                        eventName = eventName,
                        metadata = metadata,
                        screenshotPath = null,
                        memoryUsageMb = null,
                        cpuUsagePercent = null
                    )
                }
            }
        )
    }

    private fun ensureInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("MetricsSDK not initialized. Call init() first.")
        }
    }

    private fun log(message: String) {
        if (config.debugLogging) {
            Log.d(TAG, message)
        }
    }

    /**
     * Shutdown the SDK and release resources.
     * Usually not needed - SDK manages its lifecycle automatically.
     */
    @JvmStatic
    fun shutdown() {
        if (!isInitialized) return

        try {
            lifecycleTracker.stop()
            
            if (config.enableMemoryMonitoring) {
                memoryMonitor.stop()
            }
            
            if (config.enableAnrDetection) {
                anrWatchdog.stop()
            }
            
            if (config.enableCrashReporting) {
                crashHandler.uninstall()
            }
            
            screenshotManager.shutdown()
            nativeBridge.nativeReset()
            
            isInitialized = false
            log("SDK shutdown complete")
        } catch (e: Exception) {
            Log.e(TAG, "Error during shutdown", e)
        }
    }
}

package com.eslam.metrics.internal.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

/**
 * LifecycleTracker - Tracks app and activity lifecycle events
 *
 * Uses ProcessLifecycleOwner to detect foreground/background transitions.
 * Implements a grace period before ending sessions.
 */
internal class LifecycleTracker(
    private val application: Application,
    private val onForeground: () -> Unit,
    private val onBackground: () -> Unit,
    private val onActivityResumed: (Activity) -> Unit,
    private val gracePeriodMs: Long = 5000
) : DefaultLifecycleObserver, Application.ActivityLifecycleCallbacks {

    private val handler = Handler(Looper.getMainLooper())
    private var backgroundRunnable: Runnable? = null
    private var currentActivity: Activity? = null
    private var isInForeground = false

    fun start() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        application.registerActivityLifecycleCallbacks(this)
    }

    fun stop() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        application.unregisterActivityLifecycleCallbacks(this)
        backgroundRunnable?.let { handler.removeCallbacks(it) }
    }

    fun getCurrentActivity(): Activity? = currentActivity

    fun isInForeground(): Boolean = isInForeground

    // ProcessLifecycleOwner callbacks
    
    override fun onStart(owner: LifecycleOwner) {
        // App came to foreground
        backgroundRunnable?.let {
            handler.removeCallbacks(it)
            backgroundRunnable = null
        }
        
        if (!isInForeground) {
            isInForeground = true
            onForeground()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        // App went to background - start grace period
        backgroundRunnable = Runnable {
            isInForeground = false
            onBackground()
        }
        handler.postDelayed(backgroundRunnable!!, gracePeriodMs)
    }

    // ActivityLifecycleCallbacks

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // Not used
    }

    override fun onActivityStarted(activity: Activity) {
        // Not used
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
        onActivityResumed(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        if (currentActivity === activity) {
            // Keep reference for screenshot capture
        }
    }

    override fun onActivityStopped(activity: Activity) {
        // Not used
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // Not used
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity === activity) {
            currentActivity = null
        }
    }
}

package com.eslam.metrics.internal.detection

import com.eslam.metrics.internal.bridge.NativeBridge
import java.io.PrintWriter
import java.io.StringWriter

/**
 * CrashHandler - Global uncaught exception handler
 *
 * Captures crash stack traces and forwards to native layer.
 */
internal class CrashHandler(
    private val nativeBridge: NativeBridge,
    private val onCrashCaptured: (String) -> Unit
) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    private var isInstalled = false

    fun install() {
        if (!isInstalled) {
            Thread.setDefaultUncaughtExceptionHandler(this)
            isInstalled = true
        }
    }

    fun uninstall() {
        if (isInstalled) {
            Thread.setDefaultUncaughtExceptionHandler(defaultHandler)
            isInstalled = false
        }
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            val stackTrace = getStackTraceString(throwable)
            
            // Record crash in native layer
            nativeBridge.nativeRecordCrash(stackTrace)
            
            // Notify callback
            onCrashCaptured(stackTrace)
            
        } catch (e: Exception) {
            // Ignore any errors during crash handling
        }

        // Forward to default handler
        defaultHandler?.uncaughtException(thread, throwable)
    }

    private fun getStackTraceString(throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        return sw.toString()
    }
}

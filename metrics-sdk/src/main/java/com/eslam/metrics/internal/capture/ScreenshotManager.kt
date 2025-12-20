package com.eslam.metrics.internal.capture

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.view.PixelCopy
import android.view.View
import android.view.Window
import com.eslam.metrics.internal.bridge.NativeBridge
import java.io.File

/**
 * ScreenshotManager - Captures screenshots using PixelCopy
 *
 * Uses hardware-accelerated PixelCopy.request() which does NOT block the UI thread.
 * Falls back to View.draw() on older devices.
 */
internal class ScreenshotManager(
    private val nativeBridge: NativeBridge,
    private val storageDir: File
) {
    
    private val handlerThread = HandlerThread("ScreenshotThread").apply { start() }
    private val handler = Handler(handlerThread.looper)
    
    private var isLowMemory = false

    interface ScreenshotCallback {
        fun onSuccess(filePath: String)
        fun onFailure(reason: String)
    }

    fun captureScreenshot(
        activity: Activity,
        eventName: String,
        callback: ScreenshotCallback
    ) {
        // Skip if in low memory state
        if (isLowMemory || nativeBridge.nativeIsLowMemory()) {
            callback.onFailure("Low memory - screenshot skipped")
            return
        }

        val window = activity.window
        val decorView = window.decorView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            captureWithPixelCopy(window, decorView, eventName, callback)
        } else {
            captureWithViewDraw(decorView, eventName, callback)
        }
    }

    private fun captureWithPixelCopy(
        window: Window,
        view: View,
        eventName: String,
        callback: ScreenshotCallback
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            callback.onFailure("PixelCopy requires API 26+")
            return
        }

        val width = view.width
        val height = view.height

        if (width <= 0 || height <= 0) {
            callback.onFailure("Invalid view dimensions")
            return
        }

        // Create bitmap
        val bitmap = try {
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        } catch (e: OutOfMemoryError) {
            callback.onFailure("Out of memory creating bitmap")
            return
        }

        val srcRect = Rect(0, 0, width, height)

        try {
            PixelCopy.request(
                window,
                srcRect,
                bitmap,
                { copyResult ->
                    if (copyResult == PixelCopy.SUCCESS) {
                        processBitmap(bitmap, eventName, callback)
                    } else {
                        bitmap.recycle()
                        callback.onFailure("PixelCopy failed with code: $copyResult")
                    }
                },
                handler
            )
        } catch (e: Exception) {
            bitmap.recycle()
            callback.onFailure("PixelCopy exception: ${e.message}")
        }
    }

    private fun captureWithViewDraw(
        view: View,
        eventName: String,
        callback: ScreenshotCallback
    ) {
        // Fallback for older devices
        handler.post {
            try {
                val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(bitmap)
                view.draw(canvas)
                processBitmap(bitmap, eventName, callback)
            } catch (e: Exception) {
                callback.onFailure("View.draw failed: ${e.message}")
            }
        }
    }

    private fun processBitmap(
        bitmap: Bitmap,
        eventName: String,
        callback: ScreenshotCallback
    ) {
        handler.post {
            try {
                val fileName = "screenshot_${eventName}_${System.currentTimeMillis()}.jpg"
                val outputPath = File(storageDir, fileName).absolutePath

                val result = nativeBridge.nativeProcessBitmap(bitmap, outputPath)
                bitmap.recycle()

                if (result != null) {
                    callback.onSuccess(result)
                } else {
                    callback.onFailure("Native processing failed")
                }
            } catch (e: Exception) {
                bitmap.recycle()
                callback.onFailure("Processing exception: ${e.message}")
            }
        }
    }

    fun setLowMemoryState(isLowMemory: Boolean) {
        this.isLowMemory = isLowMemory
    }

    fun shutdown() {
        handlerThread.quitSafely()
    }
}

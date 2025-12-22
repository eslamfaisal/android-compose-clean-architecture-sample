package com.eslam.bakingapp.core.security

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Native Key Provider - Secure API Key Storage via NDK
 *
 * This class provides access to API keys stored securely in native (C++) code.
 * Keys are XOR-obfuscated in the native library, making them significantly
 * harder to extract through reverse engineering.
 *
 * Security Features:
 * - Keys stored in native code (ARM/x86 assembly after compilation)
 * - XOR obfuscation (keys not visible in hex editors)
 * - Package name verification (prevents use in other apps)
 * - Singleton pattern (single point of access)
 *
 * Usage:
 * ```kotlin
 * @Inject
 * lateinit var nativeKeyProvider: NativeKeyProvider
 *
 * val apiKey = nativeKeyProvider.getApiKey()
 * val secretKey = nativeKeyProvider.getSecretKey()
 * ```
 *
 * @param context Application context for package verification
 */
@Singleton
class NativeKeyProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "NativeKeyProvider"
        private const val LIBRARY_NAME = "native-keys"

        /**
         * Track if the native library was loaded successfully
         */
        @Volatile
        private var isLibraryLoaded = false

        /**
         * Error message if library loading fails
         */
        @Volatile
        private var loadError: String? = null

        init {
            loadNativeLibrary()
        }

        /**
         * Attempts to load the native library
         * Called once during class initialization
         */
        private fun loadNativeLibrary() {
            try {
                System.loadLibrary(LIBRARY_NAME)
                isLibraryLoaded = true
                Log.d(TAG, "Native library '$LIBRARY_NAME' loaded successfully")
            } catch (e: UnsatisfiedLinkError) {
                loadError = e.message
                Log.e(TAG, "Failed to load native library: ${e.message}", e)
            } catch (e: SecurityException) {
                loadError = e.message
                Log.e(TAG, "Security exception loading native library: ${e.message}", e)
            }
        }
    }

    // ==================== Native Method Declarations ====================

    /**
     * Native method to retrieve the API key
     * Package verification is performed in native code
     */
    private external fun getApiKeyNative(context: Context): String

    /**
     * Native method to retrieve the Secret key
     * Package verification is performed in native code
     */
    private external fun getSecretKeyNative(context: Context): String

    /**
     * Native method to get the app identifier
     * Built from split strings at runtime
     */
    private external fun getAppIdentifierNative(): String

    /**
     * Native method to validate key format
     * Validates without exposing actual keys
     */
    private external fun validateKeyFormatNative(keyToValidate: String): Boolean

    // ==================== Public API ====================

    /**
     * Returns true if the native library was loaded successfully
     */
    fun isAvailable(): Boolean = isLibraryLoaded

    /**
     * Returns the error message if library loading failed, null otherwise
     */
    fun getLoadError(): String? = loadError

    /**
     * Retrieves the API key from native storage
     *
     * @return The API key, or empty string if:
     *         - Library not loaded
     *         - Package verification failed
     *
     * @throws NativeLibraryNotLoadedException if library is not available
     */
    fun getApiKey(): String {
        ensureLibraryLoaded()
        return try {
            getApiKeyNative(context)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving API key: ${e.message}", e)
            ""
        }
    }

    /**
     * Retrieves the Secret key from native storage
     *
     * @return The Secret key, or empty string if:
     *         - Library not loaded
     *         - Package verification failed
     *
     * @throws NativeLibraryNotLoadedException if library is not available
     */
    fun getSecretKey(): String {
        ensureLibraryLoaded()
        return try {
            getSecretKeyNative(context)
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving Secret key: ${e.message}", e)
            ""
        }
    }

    /**
     * Retrieves the app identifier built from runtime concatenation
     *
     * @return The app identifier string
     * @throws NativeLibraryNotLoadedException if library is not available
     */
    fun getAppIdentifier(): String {
        ensureLibraryLoaded()
        return try {
            getAppIdentifierNative()
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving app identifier: ${e.message}", e)
            ""
        }
    }

    /**
     * Validates that a key matches the expected format
     * Useful for debugging without logging sensitive data
     *
     * @param key The key to validate
     * @return true if the key format is valid
     */
    fun validateKeyFormat(key: String): Boolean {
        if (!isLibraryLoaded) return false
        return try {
            validateKeyFormatNative(key)
        } catch (e: Exception) {
            Log.e(TAG, "Error validating key format: ${e.message}", e)
            false
        }
    }

    /**
     * Retrieves the API key safely, returning null instead of throwing
     *
     * @return The API key, or null if unavailable
     */
    fun getApiKeyOrNull(): String? {
        return if (isLibraryLoaded) {
            val key = getApiKey()
            key.takeIf { it.isNotEmpty() }
        } else {
            null
        }
    }

    /**
     * Retrieves the Secret key safely, returning null instead of throwing
     *
     * @return The Secret key, or null if unavailable
     */
    fun getSecretKeyOrNull(): String? {
        return if (isLibraryLoaded) {
            val key = getSecretKey()
            key.takeIf { it.isNotEmpty() }
        } else {
            null
        }
    }

    /**
     * Ensures the native library is loaded before accessing native methods
     *
     * @throws NativeLibraryNotLoadedException if the library is not loaded
     */
    private fun ensureLibraryLoaded() {
        if (!isLibraryLoaded) {
            throw NativeLibraryNotLoadedException(
                "Native library '$LIBRARY_NAME' is not loaded. Error: $loadError"
            )
        }
    }
}

/**
 * Exception thrown when native library is not available
 */
class NativeLibraryNotLoadedException(message: String) : RuntimeException(message)


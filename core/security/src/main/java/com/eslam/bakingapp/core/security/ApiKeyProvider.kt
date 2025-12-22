package com.eslam.bakingapp.core.security

/**
 * Interface for providing API keys throughout the application.
 *
 * This abstraction allows for different implementations:
 * - [NativeKeyProvider] for production (secure native storage)
 * - Mock implementations for testing
 * - BuildConfig-based implementation for development
 *
 * Usage with Hilt:
 * ```kotlin
 * @Inject
 * lateinit var apiKeyProvider: ApiKeyProvider
 *
 * val apiKey = apiKeyProvider.getApiKey()
 * ```
 */
interface ApiKeyProvider {
    /**
     * Returns true if the key provider is available and functional
     */
    fun isAvailable(): Boolean

    /**
     * Retrieves the primary API key
     * @return The API key, or empty string if unavailable
     */
    fun getApiKey(): String

    /**
     * Retrieves the secret key
     * @return The secret key, or empty string if unavailable
     */
    fun getSecretKey(): String

    /**
     * Retrieves the app identifier
     * @return The app identifier string
     */
    fun getAppIdentifier(): String

    /**
     * Validates that a key has the correct format
     * @param key The key to validate
     * @return true if the format is valid
     */
    fun validateKeyFormat(key: String): Boolean

    /**
     * Retrieves the API key, returning null if unavailable
     * @return The API key, or null if unavailable
     */
    fun getApiKeyOrNull(): String?

    /**
     * Retrieves the secret key, returning null if unavailable
     * @return The secret key, or null if unavailable
     */
    fun getSecretKeyOrNull(): String?
}

/**
 * Default implementation that delegates to [NativeKeyProvider]
 */
class DefaultApiKeyProvider(
    private val nativeKeyProvider: NativeKeyProvider
) : ApiKeyProvider {

    override fun isAvailable(): Boolean = nativeKeyProvider.isAvailable()

    override fun getApiKey(): String = nativeKeyProvider.getApiKey()

    override fun getSecretKey(): String = nativeKeyProvider.getSecretKey()

    override fun getAppIdentifier(): String = nativeKeyProvider.getAppIdentifier()

    override fun validateKeyFormat(key: String): Boolean =
        nativeKeyProvider.validateKeyFormat(key)

    override fun getApiKeyOrNull(): String? = nativeKeyProvider.getApiKeyOrNull()

    override fun getSecretKeyOrNull(): String? = nativeKeyProvider.getSecretKeyOrNull()
}


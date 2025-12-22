package com.eslam.bakingapp.core.security

import com.eslam.bakingapp.core.network.interceptor.TokenProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure token manager that implements TokenProvider interface.
 * Uses EncryptedSharedPreferences for secure token storage.
 * 
 * Security considerations:
 * - Tokens are encrypted at rest
 * - No tokens are logged
 * - Tokens can be cleared on logout or security events
 */
@Singleton
class SecureTokenManager @Inject constructor(
    private val encryptedPrefsManager: EncryptedPreferencesManager
) : TokenProvider {
    
    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
    }
    
    override fun getAccessToken(): String? {
        return encryptedPrefsManager.getString(KEY_ACCESS_TOKEN)
    }
    
    override fun getRefreshToken(): String? {
        return encryptedPrefsManager.getString(KEY_REFRESH_TOKEN)
    }
    
    override fun clearTokens() {
        encryptedPrefsManager.remove(KEY_ACCESS_TOKEN)
        encryptedPrefsManager.remove(KEY_REFRESH_TOKEN)
        encryptedPrefsManager.remove(KEY_TOKEN_EXPIRY)
    }
    
    fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresIn: Long
    ) {
        val expiryTime = System.currentTimeMillis() + (expiresIn * 1000)
        encryptedPrefsManager.putString(KEY_ACCESS_TOKEN, accessToken)
        encryptedPrefsManager.putString(KEY_REFRESH_TOKEN, refreshToken)
        encryptedPrefsManager.putLong(KEY_TOKEN_EXPIRY, expiryTime)
    }
    
    fun isTokenExpired(): Boolean {
        val expiryTime = encryptedPrefsManager.getLong(KEY_TOKEN_EXPIRY, 0L)
        return System.currentTimeMillis() >= expiryTime
    }
    
    fun hasValidToken(): Boolean {
        return getAccessToken() != null && !isTokenExpired()
    }
    
    fun saveUserInfo(userId: String, email: String, name: String) {
        encryptedPrefsManager.putString(KEY_USER_ID, userId)
        encryptedPrefsManager.putString(KEY_USER_EMAIL, email)
        encryptedPrefsManager.putString(KEY_USER_NAME, name)
    }
    
    fun getUserId(): String? = encryptedPrefsManager.getString(KEY_USER_ID)
    
    fun getUserEmail(): String? = encryptedPrefsManager.getString(KEY_USER_EMAIL)
    
    fun getUserName(): String? = encryptedPrefsManager.getString(KEY_USER_NAME)
    
    fun clearUserInfo() {
        encryptedPrefsManager.remove(KEY_USER_ID)
        encryptedPrefsManager.remove(KEY_USER_EMAIL)
        encryptedPrefsManager.remove(KEY_USER_NAME)
    }
    
    fun clearAll() {
        clearTokens()
        clearUserInfo()
    }
}





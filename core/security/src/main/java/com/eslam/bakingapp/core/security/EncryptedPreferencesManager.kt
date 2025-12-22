package com.eslam.bakingapp.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class for handling encrypted shared preferences.
 * Uses Android's EncryptedSharedPreferences for secure storage.
 * 
 * IMPORTANT: This class should be used for storing sensitive data only,
 * such as authentication tokens, user credentials, etc.
 */
@Singleton
class EncryptedPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private const val PREFS_FILE_NAME = "encrypted_prefs"
    }
    
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }
    
    val encryptedPrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    fun putString(key: String, value: String) {
        encryptedPrefs.edit().putString(key, value).apply()
    }
    
    fun getString(key: String, defaultValue: String? = null): String? {
        return encryptedPrefs.getString(key, defaultValue)
    }
    
    fun putInt(key: String, value: Int) {
        encryptedPrefs.edit().putInt(key, value).apply()
    }
    
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return encryptedPrefs.getInt(key, defaultValue)
    }
    
    fun putLong(key: String, value: Long) {
        encryptedPrefs.edit().putLong(key, value).apply()
    }
    
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return encryptedPrefs.getLong(key, defaultValue)
    }
    
    fun putBoolean(key: String, value: Boolean) {
        encryptedPrefs.edit().putBoolean(key, value).apply()
    }
    
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return encryptedPrefs.getBoolean(key, defaultValue)
    }
    
    fun remove(key: String) {
        encryptedPrefs.edit().remove(key).apply()
    }
    
    fun clear() {
        encryptedPrefs.edit().clear().apply()
    }
    
    fun contains(key: String): Boolean {
        return encryptedPrefs.contains(key)
    }
}





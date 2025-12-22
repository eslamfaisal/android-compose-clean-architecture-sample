package com.eslam.bakingapp.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that simulates network delay for testing purposes.
 * This is useful for testing loading states and UI behavior.
 * 
 * IMPORTANT: Only enable in debug builds!
 */
@Singleton
class NetworkDelayInterceptor @Inject constructor() : Interceptor {
    
    companion object {
        private const val DEFAULT_DELAY_MS = 1500L
    }
    
    var delayMs: Long = DEFAULT_DELAY_MS
    var isEnabled: Boolean = false
    
    override fun intercept(chain: Interceptor.Chain): Response {
        if (isEnabled) {
            Thread.sleep(delayMs)
        }
        return chain.proceed(chain.request())
    }
}





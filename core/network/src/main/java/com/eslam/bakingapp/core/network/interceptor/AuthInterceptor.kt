package com.eslam.bakingapp.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp Interceptor for adding authentication headers to requests.
 * This interceptor retrieves the auth token and adds it to the request header.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
) : Interceptor {
    
    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip adding auth header for certain endpoints (e.g., login, refresh)
        if (originalRequest.header("No-Auth") != null) {
            val newRequest = originalRequest.newBuilder()
                .removeHeader("No-Auth")
                .build()
            return chain.proceed(newRequest)
        }
        
        val token = tokenProvider.getAccessToken()
        
        return if (token.isNullOrEmpty()) {
            chain.proceed(originalRequest)
        } else {
            val authenticatedRequest = originalRequest.newBuilder()
                .header(AUTHORIZATION_HEADER, "$BEARER_PREFIX$token")
                .build()
            chain.proceed(authenticatedRequest)
        }
    }
}

/**
 * Interface for providing authentication tokens.
 * Implementation should be provided by the security module.
 */
interface TokenProvider {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun clearTokens()
}





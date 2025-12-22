package com.eslam.bakingapp.features.login.data.repository

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.core.security.SecureTokenManager
import com.eslam.bakingapp.features.login.domain.model.LoginCredentials
import com.eslam.bakingapp.features.login.domain.model.LoginResult
import com.eslam.bakingapp.features.login.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository.
 * Uses fake/mock data for demonstration purposes.
 * 
 * In a real app, this would use AuthApi for network calls.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val tokenManager: SecureTokenManager
    // In production, inject: private val authApi: AuthApi
) : AuthRepository {
    
    override suspend fun login(credentials: LoginCredentials): Result<LoginResult> {
        return try {
            // Simulate network delay
            delay(1500)
            
            // Fake validation - in production, this would be a network call
            if (credentials.email == "test@example.com" && credentials.password == "Password123") {
                val result = LoginResult(
                    userId = "user_123",
                    email = credentials.email,
                    name = "Test User",
                    accessToken = "fake_access_token_${System.currentTimeMillis()}",
                    refreshToken = "fake_refresh_token_${System.currentTimeMillis()}",
                    expiresIn = 3600L // 1 hour
                )
                
                // Save tokens securely
                tokenManager.saveTokens(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                    expiresIn = result.expiresIn
                )
                
                tokenManager.saveUserInfo(
                    userId = result.userId,
                    email = result.email,
                    name = result.name
                )
                
                Result.Success(result)
            } else {
                Result.Error(
                    exception = IllegalArgumentException("Invalid email or password"),
                    message = "Invalid email or password. Try test@example.com / Password123"
                )
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            // In production, call logout API
            tokenManager.clearAll()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override fun isLoggedIn(): Boolean {
        return tokenManager.hasValidToken()
    }
    
    override suspend fun refreshToken(): Result<LoginResult> {
        return try {
            val refreshToken = tokenManager.getRefreshToken()
                ?: return Result.Error(
                    exception = IllegalStateException("No refresh token available"),
                    message = "Please login again"
                )
            
            // Simulate network delay
            delay(1000)
            
            // Fake token refresh
            val result = LoginResult(
                userId = tokenManager.getUserId() ?: "",
                email = tokenManager.getUserEmail() ?: "",
                name = tokenManager.getUserName() ?: "",
                accessToken = "new_access_token_${System.currentTimeMillis()}",
                refreshToken = "new_refresh_token_${System.currentTimeMillis()}",
                expiresIn = 3600L
            )
            
            tokenManager.saveTokens(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                expiresIn = result.expiresIn
            )
            
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}





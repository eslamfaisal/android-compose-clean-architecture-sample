package com.eslam.bakingapp.features.login.data.repository

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.login.domain.model.LoginCredentials
import com.eslam.bakingapp.features.login.domain.model.LoginResult
import com.eslam.bakingapp.features.login.domain.repository.AuthRepository

/**
 * Fake implementation of AuthRepository for testing.
 */
class FakeAuthRepository : AuthRepository {
    
    var shouldReturnError = false
    var errorMessage = "Test error"
    var isLoggedIn = false
    
    private val validCredentials = LoginCredentials(
        email = "test@example.com",
        password = "Password123"
    )
    
    override suspend fun login(credentials: LoginCredentials): Result<LoginResult> {
        if (shouldReturnError) {
            return Result.Error(Exception(errorMessage), errorMessage)
        }
        
        return if (credentials == validCredentials) {
            isLoggedIn = true
            Result.Success(
                LoginResult(
                    userId = "user_123",
                    email = credentials.email,
                    name = "Test User",
                    accessToken = "fake_access_token",
                    refreshToken = "fake_refresh_token",
                    expiresIn = 3600L
                )
            )
        } else {
            Result.Error(
                IllegalArgumentException("Invalid credentials"),
                "Invalid email or password"
            )
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        if (shouldReturnError) {
            return Result.Error(Exception(errorMessage), errorMessage)
        }
        isLoggedIn = false
        return Result.Success(Unit)
    }
    
    override fun isLoggedIn(): Boolean = isLoggedIn
    
    override suspend fun refreshToken(): Result<LoginResult> {
        if (shouldReturnError) {
            return Result.Error(Exception(errorMessage), errorMessage)
        }
        
        return Result.Success(
            LoginResult(
                userId = "user_123",
                email = "test@example.com",
                name = "Test User",
                accessToken = "new_access_token",
                refreshToken = "new_refresh_token",
                expiresIn = 3600L
            )
        )
    }
}





package com.eslam.bakingapp.features.login.domain.repository

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.login.domain.model.LoginCredentials
import com.eslam.bakingapp.features.login.domain.model.LoginResult

/**
 * Repository interface for authentication operations.
 * Following the Dependency Inversion Principle from SOLID.
 */
interface AuthRepository {
    
    /**
     * Performs user login with email and password.
     */
    suspend fun login(credentials: LoginCredentials): Result<LoginResult>
    
    /**
     * Logs out the current user.
     */
    suspend fun logout(): Result<Unit>
    
    /**
     * Checks if user is currently logged in.
     */
    fun isUserAuthenticated(): Boolean
    
    /**
     * Refreshes the authentication token.
     */
    suspend fun refreshToken(): Result<LoginResult>
}




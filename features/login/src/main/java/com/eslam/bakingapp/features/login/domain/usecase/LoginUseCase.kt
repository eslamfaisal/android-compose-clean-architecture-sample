package com.eslam.bakingapp.features.login.domain.usecase

import com.eslam.bakingapp.core.common.dispatcher.IoDispatcher
import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.login.domain.model.LoginCredentials
import com.eslam.bakingapp.features.login.domain.model.LoginResult
import com.eslam.bakingapp.features.login.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for handling user login.
 * Contains the business logic for authentication.
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    /**
     * Executes the login operation.
     * 
     * @param email User's email address
     * @param password User's password
     * @return Result containing LoginResult on success or error
     */
    suspend operator fun invoke(email: String, password: String): Result<LoginResult> {
        return withContext(dispatcher) {
            val credentials = LoginCredentials(
                email = email.trim(),
                password = password
            )
            authRepository.login(credentials)
        }
    }
}





package com.eslam.bakingapp.features.login.domain.model

/**
 * Domain model representing the result of a login operation.
 */
data class LoginResult(
    val userId: String,
    val email: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

/**
 * Domain model for user credentials.
 */
data class LoginCredentials(
    val email: String,
    val password: String
)

/**
 * Validation result for login form.
 */
sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}





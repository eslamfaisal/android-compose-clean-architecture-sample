package com.eslam.bakingapp.features.login.domain.usecase

import com.eslam.bakingapp.features.login.domain.model.ValidationResult
import javax.inject.Inject

/**
 * Use case for validating email format.
 * Pure Kotlin - no Android dependencies.
 */
class ValidateEmailUseCase @Inject constructor() {
    
    companion object {
        private val EMAIL_REGEX = Regex(
            "[a-zA-Z0-9+._%\\-]{1,256}" +
            "@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
        )
    }
    
    operator fun invoke(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult.Invalid("Email cannot be empty")
        }
        
        if (!email.matches(EMAIL_REGEX)) {
            return ValidationResult.Invalid("Please enter a valid email address")
        }
        
        return ValidationResult.Valid
    }
}





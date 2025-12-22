package com.eslam.bakingapp.features.login.domain.usecase

import com.eslam.bakingapp.features.login.domain.model.ValidationResult
import javax.inject.Inject

/**
 * Use case for validating password requirements.
 * Pure Kotlin - no Android dependencies.
 */
class ValidatePasswordUseCase @Inject constructor() {
    
    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }
    
    operator fun invoke(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult.Invalid("Password cannot be empty")
        }
        
        if (password.length < MIN_PASSWORD_LENGTH) {
            return ValidationResult.Invalid("Password must be at least $MIN_PASSWORD_LENGTH characters")
        }
        
        if (!password.any { it.isDigit() }) {
            return ValidationResult.Invalid("Password must contain at least one number")
        }
        
        if (!password.any { it.isUpperCase() }) {
            return ValidationResult.Invalid("Password must contain at least one uppercase letter")
        }
        
        if (!password.any { it.isLowerCase() }) {
            return ValidationResult.Invalid("Password must contain at least one lowercase letter")
        }
        
        return ValidationResult.Valid
    }
}





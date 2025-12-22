package com.eslam.bakingapp.features.login.presentation

/**
 * UI State for the Login screen.
 * Immutable data class following MVI pattern.
 */
data class LoginUiState(
    val email: String = "test@example.com",
    val password: String = "Password123",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
) {
    /**
     * Check if form is valid for submission
     */
    val isFormValid: Boolean
        get() = email.isNotBlank() && 
                password.isNotBlank() && 
                emailError == null && 
                passwordError == null
}

/**
 * One-time events for login screen.
 */
sealed class LoginEvent {
    data class NavigateToHome(val userName: String) : LoginEvent()
    data class ShowError(val message: String) : LoginEvent()
}





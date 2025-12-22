package com.eslam.bakingapp.features.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.login.domain.model.ValidationResult
import com.eslam.bakingapp.features.login.domain.usecase.LoginUseCase
import com.eslam.bakingapp.features.login.domain.usecase.ValidateEmailUseCase
import com.eslam.bakingapp.features.login.domain.usecase.ValidatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Login screen.
 * Manages UI state and handles user interactions.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    /**
     * Called when email field changes.
     */
    fun onEmailChange(email: String) {
        _uiState.update { state ->
            state.copy(
                email = email,
                emailError = null,
                errorMessage = null
            )
        }
    }
    
    /**
     * Called when password field changes.
     */
    fun onPasswordChange(password: String) {
        _uiState.update { state ->
            state.copy(
                password = password,
                passwordError = null,
                errorMessage = null
            )
        }
    }
    
    /**
     * Validates email when focus is lost.
     */
    fun validateEmail() {
        val emailResult = validateEmailUseCase(_uiState.value.email)
        _uiState.update { state ->
            state.copy(
                emailError = when (emailResult) {
                    is ValidationResult.Invalid -> emailResult.message
                    is ValidationResult.Valid -> null
                }
            )
        }
    }
    
    /**
     * Validates password when focus is lost.
     */
    fun validatePassword() {
        val passwordResult = validatePasswordUseCase(_uiState.value.password)
        _uiState.update { state ->
            state.copy(
                passwordError = when (passwordResult) {
                    is ValidationResult.Invalid -> passwordResult.message
                    is ValidationResult.Valid -> null
                }
            )
        }
    }
    
    /**
     * Attempts to login the user.
     */
    fun onLoginClick() {
        // Validate both fields first
        val emailResult = validateEmailUseCase(_uiState.value.email)
        val passwordResult = validatePasswordUseCase(_uiState.value.password)
        
        val hasErrors = listOf(emailResult, passwordResult).any { it is ValidationResult.Invalid }
        
        _uiState.update { state ->
            state.copy(
                emailError = (emailResult as? ValidationResult.Invalid)?.message,
                passwordError = (passwordResult as? ValidationResult.Invalid)?.message
            )
        }
        
        if (hasErrors) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            when (val result = loginUseCase(_uiState.value.email, _uiState.value.password)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                    _events.send(LoginEvent.NavigateToHome(result.data.name))
                }
                
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Login failed. Please try again."
                        )
                    }
                    _events.send(LoginEvent.ShowError(result.message ?: "Login failed"))
                }
                
                is Result.Loading -> {
                    // Already handled above
                }
            }
        }
    }
    
    /**
     * Clears any displayed error message.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}





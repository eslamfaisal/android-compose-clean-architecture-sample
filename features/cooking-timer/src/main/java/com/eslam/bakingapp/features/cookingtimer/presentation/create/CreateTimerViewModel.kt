package com.eslam.bakingapp.features.cookingtimer.presentation.create

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.CreateTimerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Create Timer screen.
 * 
 * Demonstrates:
 * - Form validation
 * - User input handling
 * - Create operation with loading state
 */
@HiltViewModel
class CreateTimerViewModel @Inject constructor(
    private val createTimerUseCase: CreateTimerUseCase
) : ViewModel() {
    
    companion object {
        private const val TAG = "CreateTimerViewModel"
        private const val MAX_HOURS = 23
        private const val MAX_MINUTES = 59
        private const val MAX_SECONDS = 59
    }
    
    private val _uiState = MutableStateFlow(CreateTimerUiState())
    val uiState: StateFlow<CreateTimerUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<CreateTimerEvent>()
    val events: SharedFlow<CreateTimerEvent> = _events.asSharedFlow()
    
    fun onNameChanged(name: String) {
        _uiState.update { state ->
            state.copy(
                name = name,
                nameError = if (name.isBlank()) "Timer name is required" else null
            )
        }
    }
    
    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description) }
    }
    
    fun onHoursChanged(hours: Int) {
        _uiState.update { state ->
            val validHours = hours.coerceIn(0, MAX_HOURS)
            state.copy(
                hours = validHours,
                durationError = validateDuration(validHours, state.minutes, state.seconds)
            )
        }
    }
    
    fun onMinutesChanged(minutes: Int) {
        _uiState.update { state ->
            val validMinutes = minutes.coerceIn(0, MAX_MINUTES)
            state.copy(
                minutes = validMinutes,
                durationError = validateDuration(state.hours, validMinutes, state.seconds)
            )
        }
    }
    
    fun onSecondsChanged(seconds: Int) {
        _uiState.update { state ->
            val validSeconds = seconds.coerceIn(0, MAX_SECONDS)
            state.copy(
                seconds = validSeconds,
                durationError = validateDuration(state.hours, state.minutes, validSeconds)
            )
        }
    }
    
    private fun validateDuration(hours: Int, minutes: Int, seconds: Int): String? {
        val total = (hours * 3600L) + (minutes * 60L) + seconds
        return when {
            total <= 0 -> "Duration must be greater than 0"
            total > 24 * 60 * 60 -> "Duration cannot exceed 24 hours"
            else -> null
        }
    }
    
    fun onCreateTimer() {
        val state = _uiState.value
        
        // Final validation
        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Timer name is required") }
            return
        }
        
        if (state.totalSeconds <= 0) {
            _uiState.update { it.copy(durationError = "Duration must be greater than 0") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true) }
            
            val result = createTimerUseCase(
                name = state.name,
                description = state.description,
                durationSeconds = state.totalSeconds
            )
            
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "Timer created: ${result.data.id}")
                    _events.emit(CreateTimerEvent.TimerCreated(result.data.id))
                    _events.emit(CreateTimerEvent.ShowMessage("Timer created"))
                    _events.emit(CreateTimerEvent.NavigateBack)
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isCreating = false) }
                    _events.emit(CreateTimerEvent.ShowMessage(result.message ?: "Failed to create timer"))
                }
                is Result.Loading -> {}
            }
        }
    }
    
    fun onCancelClick() {
        viewModelScope.launch {
            _events.emit(CreateTimerEvent.NavigateBack)
        }
    }
}


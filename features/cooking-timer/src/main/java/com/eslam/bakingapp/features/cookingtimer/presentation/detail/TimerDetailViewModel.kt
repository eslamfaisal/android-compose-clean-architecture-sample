package com.eslam.bakingapp.features.cookingtimer.presentation.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerStatus
import com.eslam.bakingapp.features.cookingtimer.domain.repository.TimerRepository
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.DeleteTimerUseCase
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.GetTimerByIdUseCase
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.PauseTimerUseCase
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.ResetTimerUseCase
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.StartTimerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Timer Detail screen.
 * 
 * Demonstrates:
 * - SavedStateHandle for navigation arguments
 * - Process death survival with SavedStateHandle
 * - Coroutine job management
 */
@HiltViewModel
class TimerDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTimerByIdUseCase: GetTimerByIdUseCase,
    private val startTimerUseCase: StartTimerUseCase,
    private val pauseTimerUseCase: PauseTimerUseCase,
    private val resetTimerUseCase: ResetTimerUseCase,
    private val deleteTimerUseCase: DeleteTimerUseCase,
    private val repository: TimerRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "TimerDetailViewModel"
    }
    
    /**
     * Timer ID from navigation arguments.
     * SavedStateHandle survives process death.
     */
    private val timerId: String = savedStateHandle.get<String>("timerId")
        ?: throw IllegalArgumentException("Timer ID is required")
    
    private val _uiState = MutableStateFlow(TimerDetailUiState())
    val uiState: StateFlow<TimerDetailUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<TimerDetailEvent>()
    val events: SharedFlow<TimerDetailEvent> = _events.asSharedFlow()
    
    private var timerTickJob: Job? = null
    
    init {
        Log.d(TAG, "ViewModel initialized with timerId: $timerId")
        loadTimer()
        startTimerTick()
    }
    
    private fun loadTimer() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = getTimerByIdUseCase(timerId)) {
                is Result.Success -> {
                    _uiState.update { it.copy(timer = result.data, isLoading = false) }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = result.message ?: "Timer not found"
                        ) 
                    }
                }
                is Result.Loading -> {}
            }
        }
    }
    
    private fun startTimerTick() {
        timerTickJob?.cancel()
        timerTickJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                updateTimer()
            }
        }
    }
    
    private suspend fun updateTimer() {
        val currentTimer = _uiState.value.timer ?: return
        if (!currentTimer.isRunning) return
        
        val newRemainingTime = currentTimer.remainingSeconds - 1
        
        if (newRemainingTime <= 0) {
            repository.updateRemainingTime(currentTimer.id, 0)
            _events.emit(TimerDetailEvent.ShowMessage("Timer completed!"))
        } else {
            repository.updateRemainingTime(currentTimer.id, newRemainingTime)
        }
        
        // Reload timer to get updated state
        when (val result = getTimerByIdUseCase(timerId)) {
            is Result.Success -> {
                _uiState.update { it.copy(timer = result.data) }
            }
            else -> {}
        }
    }
    
    fun onStartTimer() {
        viewModelScope.launch {
            when (startTimerUseCase(timerId)) {
                is Result.Success -> loadTimer()
                is Result.Error -> _events.emit(TimerDetailEvent.ShowMessage("Failed to start timer"))
                is Result.Loading -> {}
            }
        }
    }
    
    fun onPauseTimer() {
        viewModelScope.launch {
            when (pauseTimerUseCase(timerId)) {
                is Result.Success -> loadTimer()
                is Result.Error -> _events.emit(TimerDetailEvent.ShowMessage("Failed to pause timer"))
                is Result.Loading -> {}
            }
        }
    }
    
    fun onResetTimer() {
        viewModelScope.launch {
            when (resetTimerUseCase(timerId)) {
                is Result.Success -> {
                    loadTimer()
                    _events.emit(TimerDetailEvent.ShowMessage("Timer reset"))
                }
                is Result.Error -> _events.emit(TimerDetailEvent.ShowMessage("Failed to reset timer"))
                is Result.Loading -> {}
            }
        }
    }
    
    fun onDeleteTimer() {
        viewModelScope.launch {
            when (deleteTimerUseCase(timerId)) {
                is Result.Success -> {
                    _events.emit(TimerDetailEvent.ShowMessage("Timer deleted"))
                    _events.emit(TimerDetailEvent.NavigateBack)
                }
                is Result.Error -> _events.emit(TimerDetailEvent.ShowMessage("Failed to delete timer"))
                is Result.Loading -> {}
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared")
        timerTickJob?.cancel()
    }
}


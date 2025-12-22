package com.eslam.bakingapp.features.cookingtimer.presentation.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.cookingtimer.domain.model.CookingTimer
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerStatus
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.DeleteTimerUseCase
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.GetTimersUseCase
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.PauseTimerUseCase
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.ResetTimerUseCase
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.StartTimerUseCase
import com.eslam.bakingapp.features.cookingtimer.domain.repository.TimerRepository
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
 * ViewModel for the Timer List screen.
 * 
 * ## ViewModel Lifecycle Concepts Demonstrated:
 * 
 * 1. **viewModelScope**: Coroutine scope tied to ViewModel lifecycle.
 *    All coroutines are automatically cancelled when ViewModel is cleared.
 * 
 * 2. **StateFlow**: Hot flow for UI state, survives configuration changes.
 * 
 * 3. **SharedFlow**: For one-time events (navigation, snackbars).
 * 
 * 4. **onCleared()**: Called when ViewModel is no longer used.
 * 
 * ## Key Points:
 * - ViewModel survives configuration changes (rotation)
 * - ViewModel is scoped to navigation graph or activity/fragment
 * - Never hold references to Views, Activities, or Context
 * - Use SavedStateHandle for process death survival
 */
@HiltViewModel
class TimerListViewModel @Inject constructor(
    private val getTimersUseCase: GetTimersUseCase,
    private val startTimerUseCase: StartTimerUseCase,
    private val pauseTimerUseCase: PauseTimerUseCase,
    private val resetTimerUseCase: ResetTimerUseCase,
    private val deleteTimerUseCase: DeleteTimerUseCase,
    private val repository: TimerRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "TimerListViewModel"
        private const val TIMER_TICK_INTERVAL_MS = 1000L
    }
    
    // UI State - survives configuration changes
    private val _uiState = MutableStateFlow(TimerListUiState())
    val uiState: StateFlow<TimerListUiState> = _uiState.asStateFlow()
    
    // One-time events - consumed once by UI
    private val _events = MutableSharedFlow<TimerListEvent>()
    val events: SharedFlow<TimerListEvent> = _events.asSharedFlow()
    
    // Timer tick job - manages countdown updates
    private var timerTickJob: Job? = null
    
    init {
        Log.d(TAG, "ViewModel initialized")
        loadTimers()
        startTimerTick()
    }
    
    /**
     * Load all timers from repository.
     */
    private fun loadTimers() {
        viewModelScope.launch {
            getTimersUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Result.Success -> {
                        val activeCount = result.data.count { 
                            it.status == TimerStatus.RUNNING || it.status == TimerStatus.PAUSED 
                        }
                        _uiState.update { state ->
                            state.copy(
                                timers = result.data,
                                isLoading = false,
                                errorMessage = null,
                                activeTimersCount = activeCount
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                errorMessage = result.message ?: "Failed to load timers"
                            )
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Start the timer tick job.
     * Updates all running timers every second.
     */
    private fun startTimerTick() {
        timerTickJob?.cancel()
        timerTickJob = viewModelScope.launch {
            while (isActive) {
                delay(TIMER_TICK_INTERVAL_MS)
                updateRunningTimers()
            }
        }
    }
    
    /**
     * Update all running timers by decrementing their remaining time.
     */
    private suspend fun updateRunningTimers() {
        val runningTimers = _uiState.value.timers.filter { it.isRunning }
        
        for (timer in runningTimers) {
            val newRemainingTime = timer.remainingSeconds - 1
            
            if (newRemainingTime <= 0) {
                // Timer completed
                repository.updateRemainingTime(timer.id, 0)
                _events.emit(TimerListEvent.TimerCompleted(timer))
                _events.emit(TimerListEvent.ShowMessage("${timer.name} completed!"))
            } else {
                repository.updateRemainingTime(timer.id, newRemainingTime)
            }
        }
    }
    
    // ===========================================
    // USER ACTIONS
    // ===========================================
    
    /**
     * Start a timer.
     */
    fun onStartTimer(timerId: String) {
        viewModelScope.launch {
            when (val result = startTimerUseCase(timerId)) {
                is Result.Success -> {
                    Log.d(TAG, "Timer $timerId started")
                }
                is Result.Error -> {
                    _events.emit(TimerListEvent.ShowMessage(result.message ?: "Failed to start timer"))
                }
                is Result.Loading -> {}
            }
        }
    }
    
    /**
     * Pause a timer.
     */
    fun onPauseTimer(timerId: String) {
        viewModelScope.launch {
            when (val result = pauseTimerUseCase(timerId)) {
                is Result.Success -> {
                    Log.d(TAG, "Timer $timerId paused")
                }
                is Result.Error -> {
                    _events.emit(TimerListEvent.ShowMessage(result.message ?: "Failed to pause timer"))
                }
                is Result.Loading -> {}
            }
        }
    }
    
    /**
     * Reset a timer.
     */
    fun onResetTimer(timerId: String) {
        viewModelScope.launch {
            when (val result = resetTimerUseCase(timerId)) {
                is Result.Success -> {
                    Log.d(TAG, "Timer $timerId reset")
                    _events.emit(TimerListEvent.ShowMessage("Timer reset"))
                }
                is Result.Error -> {
                    _events.emit(TimerListEvent.ShowMessage(result.message ?: "Failed to reset timer"))
                }
                is Result.Loading -> {}
            }
        }
    }
    
    /**
     * Delete a timer.
     */
    fun onDeleteTimer(timerId: String) {
        viewModelScope.launch {
            when (val result = deleteTimerUseCase(timerId)) {
                is Result.Success -> {
                    Log.d(TAG, "Timer $timerId deleted")
                    _events.emit(TimerListEvent.ShowMessage("Timer deleted"))
                }
                is Result.Error -> {
                    _events.emit(TimerListEvent.ShowMessage(result.message ?: "Failed to delete timer"))
                }
                is Result.Loading -> {}
            }
        }
    }
    
    /**
     * Navigate to timer detail.
     */
    fun onTimerClick(timer: CookingTimer) {
        viewModelScope.launch {
            _events.emit(TimerListEvent.NavigateToDetail(timer.id))
        }
    }
    
    /**
     * Navigate to create timer screen.
     */
    fun onCreateTimerClick() {
        viewModelScope.launch {
            _events.emit(TimerListEvent.NavigateToCreate)
        }
    }
    
    /**
     * Navigate to presets screen.
     */
    fun onPresetsClick() {
        viewModelScope.launch {
            _events.emit(TimerListEvent.NavigateToPresets)
        }
    }
    
    /**
     * Clear error message.
     */
    fun onErrorDismissed() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    // ===========================================
    // LIFECYCLE: onCleared
    // ===========================================
    
    /**
     * Called when this ViewModel is no longer used and will be destroyed.
     * 
     * Use this to:
     * - Cancel all ongoing work (handled automatically by viewModelScope)
     * - Release resources
     * - Clean up subscriptions
     */
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared - all coroutines in viewModelScope are cancelled")
        
        // timerTickJob is automatically cancelled with viewModelScope
        // But explicit cancellation is fine for clarity
        timerTickJob?.cancel()
    }
}


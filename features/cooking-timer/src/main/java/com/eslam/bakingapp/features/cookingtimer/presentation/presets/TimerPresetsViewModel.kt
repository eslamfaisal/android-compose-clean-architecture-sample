package com.eslam.bakingapp.features.cookingtimer.presentation.presets

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.cookingtimer.domain.model.PresetCategory
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerPreset
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.CreateTimerUseCase
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.GetTimerPresetsUseCase
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
 * ViewModel for Timer Presets screen.
 */
@HiltViewModel
class TimerPresetsViewModel @Inject constructor(
    private val getTimerPresetsUseCase: GetTimerPresetsUseCase,
    private val createTimerUseCase: CreateTimerUseCase
) : ViewModel() {
    
    companion object {
        private const val TAG = "TimerPresetsViewModel"
    }
    
    private val _uiState = MutableStateFlow(TimerPresetsUiState())
    val uiState: StateFlow<TimerPresetsUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<TimerPresetsEvent>()
    val events: SharedFlow<TimerPresetsEvent> = _events.asSharedFlow()
    
    init {
        loadPresets()
    }
    
    private fun loadPresets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = getTimerPresetsUseCase()) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            presets = result.data,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Failed to load presets"
                        )
                    }
                }
                is Result.Loading -> {}
            }
        }
    }
    
    fun onCategorySelected(category: PresetCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
    
    fun onPresetClick(preset: TimerPreset) {
        viewModelScope.launch {
            val result = createTimerUseCase(
                name = preset.name,
                description = "Created from ${preset.category.toDisplayString()} preset",
                durationSeconds = preset.durationSeconds
            )
            
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "Timer created from preset: ${result.data.id}")
                    _events.emit(TimerPresetsEvent.TimerCreatedFromPreset(result.data.id))
                    _events.emit(TimerPresetsEvent.ShowMessage("Timer '${preset.name}' created"))
                    _events.emit(TimerPresetsEvent.NavigateBack)
                }
                is Result.Error -> {
                    _events.emit(TimerPresetsEvent.ShowMessage(result.message ?: "Failed to create timer"))
                }
                is Result.Loading -> {}
            }
        }
    }
}


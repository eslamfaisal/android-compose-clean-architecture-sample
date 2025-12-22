package com.eslam.bakingapp.features.cookingtimer.presentation.presets

import com.eslam.bakingapp.features.cookingtimer.domain.model.PresetCategory
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerPreset

/**
 * UI State for Timer Presets screen.
 */
data class TimerPresetsUiState(
    val presets: List<TimerPreset> = emptyList(),
    val selectedCategory: PresetCategory? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    /**
     * Presets grouped by category.
     */
    val groupedPresets: Map<PresetCategory, List<TimerPreset>>
        get() = presets.groupBy { it.category }
    
    /**
     * Filtered presets based on selected category.
     */
    val filteredPresets: List<TimerPreset>
        get() = if (selectedCategory == null) {
            presets
        } else {
            presets.filter { it.category == selectedCategory }
        }
    
    /**
     * Available categories.
     */
    val categories: List<PresetCategory>
        get() = PresetCategory.entries.toList()
}

/**
 * One-time events for Timer Presets screen.
 */
sealed class TimerPresetsEvent {
    data object NavigateBack : TimerPresetsEvent()
    data class ShowMessage(val message: String) : TimerPresetsEvent()
    data class TimerCreatedFromPreset(val timerId: String) : TimerPresetsEvent()
}


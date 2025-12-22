package com.eslam.bakingapp.features.cookingtimer.presentation.detail

import com.eslam.bakingapp.features.cookingtimer.domain.model.CookingTimer

/**
 * UI State for Timer Detail screen.
 */
data class TimerDetailUiState(
    val timer: CookingTimer? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val hasTimer: Boolean
        get() = timer != null && !isLoading && errorMessage == null
}

/**
 * One-time events for Timer Detail screen.
 */
sealed class TimerDetailEvent {
    data object NavigateBack : TimerDetailEvent()
    data class ShowMessage(val message: String) : TimerDetailEvent()
}


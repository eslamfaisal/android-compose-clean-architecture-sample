package com.eslam.bakingapp.features.cookingtimer.presentation.list

import com.eslam.bakingapp.features.cookingtimer.domain.model.CookingTimer

/**
 * UI State for the Timer List screen.
 * 
 * This sealed interface represents all possible states
 * the UI can be in, following the MVI (Model-View-Intent) pattern.
 */
data class TimerListUiState(
    val timers: List<CookingTimer> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val activeTimersCount: Int = 0
) {
    /**
     * Check if the list is empty (not loading and no timers).
     */
    val isEmpty: Boolean
        get() = !isLoading && timers.isEmpty() && errorMessage == null
    
    /**
     * Check if there's content to show.
     */
    val hasContent: Boolean
        get() = timers.isNotEmpty()
}

/**
 * One-time events emitted by the ViewModel.
 * These are consumed once by the UI (navigation, snackbars, etc.)
 */
sealed class TimerListEvent {
    data class NavigateToDetail(val timerId: String) : TimerListEvent()
    data object NavigateToCreate : TimerListEvent()
    data object NavigateToPresets : TimerListEvent()
    data class ShowMessage(val message: String) : TimerListEvent()
    data class TimerCompleted(val timer: CookingTimer) : TimerListEvent()
}


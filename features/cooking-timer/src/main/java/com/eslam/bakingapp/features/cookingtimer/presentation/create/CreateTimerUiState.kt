package com.eslam.bakingapp.features.cookingtimer.presentation.create

/**
 * UI State for Create Timer screen.
 */
data class CreateTimerUiState(
    val name: String = "",
    val description: String = "",
    val hours: Int = 0,
    val minutes: Int = 5,
    val seconds: Int = 0,
    val isCreating: Boolean = false,
    val nameError: String? = null,
    val durationError: String? = null
) {
    val isValid: Boolean
        get() = name.isNotBlank() && totalSeconds > 0 && nameError == null && durationError == null
    
    val totalSeconds: Long
        get() = (hours * 3600L) + (minutes * 60L) + seconds
    
    val formattedDuration: String
        get() = if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
}

/**
 * One-time events for Create Timer screen.
 */
sealed class CreateTimerEvent {
    data object NavigateBack : CreateTimerEvent()
    data class ShowMessage(val message: String) : CreateTimerEvent()
    data class TimerCreated(val timerId: String) : CreateTimerEvent()
}


package com.eslam.bakingapp.features.cookingtimer.domain.usecase

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.cookingtimer.domain.model.CookingTimer
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerStatus
import com.eslam.bakingapp.features.cookingtimer.domain.repository.TimerRepository
import javax.inject.Inject

/**
 * Use case for creating a new timer.
 */
class CreateTimerUseCase @Inject constructor(
    private val repository: TimerRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String,
        durationSeconds: Long,
        recipeId: String? = null,
        stepNumber: Int? = null
    ): Result<CookingTimer> {
        // Validate input
        if (name.isBlank()) {
            return Result.Error(IllegalArgumentException("Timer name cannot be empty"))
        }
        if (durationSeconds <= 0) {
            return Result.Error(IllegalArgumentException("Timer duration must be greater than 0"))
        }
        if (durationSeconds > 24 * 60 * 60) { // Max 24 hours
            return Result.Error(IllegalArgumentException("Timer duration cannot exceed 24 hours"))
        }
        
        val timer = CookingTimer(
            id = System.currentTimeMillis().toString(),
            name = name.trim(),
            description = description.trim(),
            durationSeconds = durationSeconds,
            remainingSeconds = durationSeconds,
            status = TimerStatus.IDLE,
            recipeId = recipeId,
            stepNumber = stepNumber
        )
        
        return repository.createTimer(timer)
    }
}

/**
 * Use case for starting a timer.
 */
class StartTimerUseCase @Inject constructor(
    private val repository: TimerRepository
) {
    suspend operator fun invoke(timerId: String): Result<Unit> {
        return repository.updateTimerStatus(timerId, TimerStatus.RUNNING)
    }
}

/**
 * Use case for pausing a timer.
 */
class PauseTimerUseCase @Inject constructor(
    private val repository: TimerRepository
) {
    suspend operator fun invoke(timerId: String): Result<Unit> {
        return repository.updateTimerStatus(timerId, TimerStatus.PAUSED)
    }
}

/**
 * Use case for resuming a paused timer.
 */
class ResumeTimerUseCase @Inject constructor(
    private val repository: TimerRepository
) {
    suspend operator fun invoke(timerId: String): Result<Unit> {
        return repository.updateTimerStatus(timerId, TimerStatus.RUNNING)
    }
}

/**
 * Use case for stopping/cancelling a timer.
 */
class StopTimerUseCase @Inject constructor(
    private val repository: TimerRepository
) {
    suspend operator fun invoke(timerId: String): Result<Unit> {
        return repository.updateTimerStatus(timerId, TimerStatus.CANCELLED)
    }
}

/**
 * Use case for resetting a timer to its initial state.
 */
class ResetTimerUseCase @Inject constructor(
    private val repository: TimerRepository
) {
    suspend operator fun invoke(timerId: String): Result<Unit> {
        val timerResult = repository.getTimerById(timerId)
        
        return when (timerResult) {
            is Result.Success -> {
                val timer = timerResult.data
                repository.updateTimer(
                    timer.copy(
                        remainingSeconds = timer.durationSeconds,
                        status = TimerStatus.IDLE
                    )
                ).let { 
                    when (it) {
                        is Result.Success -> Result.Success(Unit)
                        is Result.Error -> Result.Error(it.exception, it.message)
                        is Result.Loading -> Result.Loading
                    }
                }
            }
            is Result.Error -> Result.Error(timerResult.exception, timerResult.message)
            is Result.Loading -> Result.Loading
        }
    }
}

/**
 * Use case for deleting a timer.
 */
class DeleteTimerUseCase @Inject constructor(
    private val repository: TimerRepository
) {
    suspend operator fun invoke(timerId: String): Result<Unit> {
        return repository.deleteTimer(timerId)
    }
}

/**
 * Use case for clearing all completed timers.
 */
class ClearCompletedTimersUseCase @Inject constructor(
    private val repository: TimerRepository
) {
    suspend operator fun invoke(): Result<Int> {
        return repository.clearCompletedTimers()
    }
}


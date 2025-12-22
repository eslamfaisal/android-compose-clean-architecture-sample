package com.eslam.bakingapp.features.cookingtimer.domain.usecase

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.cookingtimer.domain.model.CookingTimer
import com.eslam.bakingapp.features.cookingtimer.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all cooking timers.
 * 
 * This demonstrates the Single Responsibility Principle -
 * each use case handles one specific operation.
 */
class GetTimersUseCase @Inject constructor(
    private val repository: TimerRepository
) {
    /**
     * Invoke operator allows calling the use case as a function.
     * 
     * @return Flow of Result containing list of timers
     */
    operator fun invoke(): Flow<Result<List<CookingTimer>>> {
        return repository.getAllTimers()
    }
}

/**
 * Use case for getting active timers only.
 */
class GetActiveTimersUseCase @Inject constructor(
    private val repository: TimerRepository
) {
    operator fun invoke(): Flow<Result<List<CookingTimer>>> {
        return repository.getActiveTimers()
    }
}

/**
 * Use case for getting a specific timer by ID.
 */
class GetTimerByIdUseCase @Inject constructor(
    private val repository: TimerRepository
) {
    suspend operator fun invoke(timerId: String): Result<CookingTimer> {
        return repository.getTimerById(timerId)
    }
}


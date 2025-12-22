package com.eslam.bakingapp.features.cookingtimer.domain.repository

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.cookingtimer.domain.model.CookingTimer
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerPreset
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing cooking timers.
 * 
 * This follows the Repository pattern from Clean Architecture,
 * providing a clean abstraction over data sources.
 */
interface TimerRepository {
    
    /**
     * Get all timers as a Flow for real-time updates.
     * This demonstrates reactive data streams that work well
     * with lifecycle-aware components.
     */
    fun getAllTimers(): Flow<Result<List<CookingTimer>>>
    
    /**
     * Get a specific timer by ID.
     */
    suspend fun getTimerById(timerId: String): Result<CookingTimer>
    
    /**
     * Get active (running or paused) timers.
     */
    fun getActiveTimers(): Flow<Result<List<CookingTimer>>>
    
    /**
     * Create a new timer.
     */
    suspend fun createTimer(timer: CookingTimer): Result<CookingTimer>
    
    /**
     * Update an existing timer.
     */
    suspend fun updateTimer(timer: CookingTimer): Result<CookingTimer>
    
    /**
     * Update timer status.
     */
    suspend fun updateTimerStatus(timerId: String, status: TimerStatus): Result<Unit>
    
    /**
     * Update remaining time for a timer.
     */
    suspend fun updateRemainingTime(timerId: String, remainingSeconds: Long): Result<Unit>
    
    /**
     * Delete a timer.
     */
    suspend fun deleteTimer(timerId: String): Result<Unit>
    
    /**
     * Get all available timer presets.
     */
    suspend fun getTimerPresets(): Result<List<TimerPreset>>
    
    /**
     * Clear all completed timers.
     */
    suspend fun clearCompletedTimers(): Result<Int>
}


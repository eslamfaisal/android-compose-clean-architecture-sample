package com.eslam.bakingapp.features.cookingtimer.data.repository

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.cookingtimer.domain.model.CookingTimer
import com.eslam.bakingapp.features.cookingtimer.domain.model.PresetCategory
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerPreset
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerStatus
import com.eslam.bakingapp.features.cookingtimer.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fake implementation of TimerRepository for testing.
 * 
 * This demonstrates the Test Double pattern - using a fake
 * implementation instead of mocks for more realistic testing.
 */
class FakeTimerRepository : TimerRepository {
    
    private val timers = MutableStateFlow<MutableMap<String, CookingTimer>>(mutableMapOf())
    
    var shouldReturnError = false
    var errorMessage = "Test error"
    
    private fun createError(message: String): Result.Error {
        return Result.Error(Exception(message), message)
    }
    
    fun addTimer(timer: CookingTimer) {
        timers.value = timers.value.toMutableMap().apply { put(timer.id, timer) }
    }
    
    fun clearTimers() {
        timers.value = mutableMapOf()
    }
    
    override fun getAllTimers(): Flow<Result<List<CookingTimer>>> {
        return timers.map { map ->
            if (shouldReturnError) {
                createError(errorMessage)
            } else {
                Result.Success(map.values.toList())
            }
        }
    }
    
    override suspend fun getTimerById(timerId: String): Result<CookingTimer> {
        if (shouldReturnError) return createError(errorMessage)
        val timer = timers.value[timerId]
        return if (timer != null) {
            Result.Success(timer)
        } else {
            createError("Timer not found")
        }
    }
    
    override fun getActiveTimers(): Flow<Result<List<CookingTimer>>> {
        return timers.map { map ->
            if (shouldReturnError) {
                createError(errorMessage)
            } else {
                val active = map.values.filter { 
                    it.status == TimerStatus.RUNNING || it.status == TimerStatus.PAUSED 
                }
                Result.Success(active)
            }
        }
    }
    
    override suspend fun createTimer(timer: CookingTimer): Result<CookingTimer> {
        if (shouldReturnError) return createError(errorMessage)
        timers.value = timers.value.toMutableMap().apply { put(timer.id, timer) }
        return Result.Success(timer)
    }
    
    override suspend fun updateTimer(timer: CookingTimer): Result<CookingTimer> {
        if (shouldReturnError) return createError(errorMessage)
        timers.value = timers.value.toMutableMap().apply { put(timer.id, timer) }
        return Result.Success(timer)
    }
    
    override suspend fun updateTimerStatus(timerId: String, status: TimerStatus): Result<Unit> {
        if (shouldReturnError) return createError(errorMessage)
        val timer = timers.value[timerId] ?: return createError("Timer not found")
        timers.value = timers.value.toMutableMap().apply { 
            put(timerId, timer.copy(status = status)) 
        }
        return Result.Success(Unit)
    }
    
    override suspend fun updateRemainingTime(timerId: String, remainingSeconds: Long): Result<Unit> {
        if (shouldReturnError) return createError(errorMessage)
        val timer = timers.value[timerId] ?: return createError("Timer not found")
        timers.value = timers.value.toMutableMap().apply {
            put(timerId, timer.copy(remainingSeconds = remainingSeconds))
        }
        return Result.Success(Unit)
    }
    
    override suspend fun deleteTimer(timerId: String): Result<Unit> {
        if (shouldReturnError) return createError(errorMessage)
        timers.value = timers.value.toMutableMap().apply { remove(timerId) }
        return Result.Success(Unit)
    }
    
    override suspend fun getTimerPresets(): Result<List<TimerPreset>> {
        if (shouldReturnError) return createError(errorMessage)
        return Result.Success(
            listOf(
                TimerPreset("1", "Test Preset 1", 60, PresetCategory.BOILING),
                TimerPreset("2", "Test Preset 2", 120, PresetCategory.BAKING)
            )
        )
    }
    
    override suspend fun clearCompletedTimers(): Result<Int> {
        if (shouldReturnError) return createError(errorMessage)
        val completed = timers.value.values.count { 
            it.status == TimerStatus.COMPLETED || it.status == TimerStatus.CANCELLED 
        }
        timers.value = timers.value.filterValues { 
            it.status != TimerStatus.COMPLETED && it.status != TimerStatus.CANCELLED 
        }.toMutableMap()
        return Result.Success(completed)
    }
}


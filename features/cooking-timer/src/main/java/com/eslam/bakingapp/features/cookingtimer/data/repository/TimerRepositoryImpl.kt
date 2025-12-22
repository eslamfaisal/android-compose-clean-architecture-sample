package com.eslam.bakingapp.features.cookingtimer.data.repository

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.cookingtimer.data.datasource.LocalTimerDataSource
import com.eslam.bakingapp.features.cookingtimer.domain.model.CookingTimer
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerPreset
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerStatus
import com.eslam.bakingapp.features.cookingtimer.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of TimerRepository.
 * 
 * This class bridges the domain layer with the data layer,
 * handling data transformations and error wrapping.
 */
@Singleton
class TimerRepositoryImpl @Inject constructor(
    private val localDataSource: LocalTimerDataSource
) : TimerRepository {
    
    override fun getAllTimers(): Flow<Result<List<CookingTimer>>> {
        return localDataSource.getAllTimers()
            .map<List<CookingTimer>, Result<List<CookingTimer>>> { timers ->
                Result.Success(timers)
            }
            .onStart { emit(Result.Loading) }
            .catch { e ->
                emit(Result.Error(e, e.message ?: "Failed to load timers"))
            }
    }
    
    override suspend fun getTimerById(timerId: String): Result<CookingTimer> {
        return try {
            val timer = localDataSource.getTimerById(timerId)
            if (timer != null) {
                Result.Success(timer)
            } else {
                Result.Error(NoSuchElementException("Timer not found"))
            }
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed to get timer")
        }
    }
    
    override fun getActiveTimers(): Flow<Result<List<CookingTimer>>> {
        return localDataSource.getActiveTimers()
            .map<List<CookingTimer>, Result<List<CookingTimer>>> { timers ->
                Result.Success(timers)
            }
            .onStart { emit(Result.Loading) }
            .catch { e ->
                emit(Result.Error(e, e.message ?: "Failed to load active timers"))
            }
    }
    
    override suspend fun createTimer(timer: CookingTimer): Result<CookingTimer> {
        return try {
            localDataSource.insertTimer(timer)
            Result.Success(timer)
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed to create timer")
        }
    }
    
    override suspend fun updateTimer(timer: CookingTimer): Result<CookingTimer> {
        return try {
            localDataSource.updateTimer(timer)
            Result.Success(timer)
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed to update timer")
        }
    }
    
    override suspend fun updateTimerStatus(timerId: String, status: TimerStatus): Result<Unit> {
        return try {
            val timer = localDataSource.getTimerById(timerId)
            if (timer != null) {
                localDataSource.updateTimer(timer.copy(status = status))
                Result.Success(Unit)
            } else {
                Result.Error(NoSuchElementException("Timer not found"))
            }
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed to update timer status")
        }
    }
    
    override suspend fun updateRemainingTime(timerId: String, remainingSeconds: Long): Result<Unit> {
        return try {
            val timer = localDataSource.getTimerById(timerId)
            if (timer != null) {
                val newStatus = if (remainingSeconds <= 0) {
                    TimerStatus.COMPLETED
                } else {
                    timer.status
                }
                localDataSource.updateTimer(
                    timer.copy(
                        remainingSeconds = maxOf(0, remainingSeconds),
                        status = newStatus
                    )
                )
                Result.Success(Unit)
            } else {
                Result.Error(NoSuchElementException("Timer not found"))
            }
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed to update remaining time")
        }
    }
    
    override suspend fun deleteTimer(timerId: String): Result<Unit> {
        return try {
            localDataSource.deleteTimer(timerId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed to delete timer")
        }
    }
    
    override suspend fun getTimerPresets(): Result<List<TimerPreset>> {
        return try {
            Result.Success(localDataSource.getTimerPresets())
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed to load presets")
        }
    }
    
    override suspend fun clearCompletedTimers(): Result<Int> {
        return try {
            val count = localDataSource.clearCompletedTimers()
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error(e, e.message ?: "Failed to clear completed timers")
        }
    }
}


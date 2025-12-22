package com.eslam.bakingapp.features.cookingtimer.domain.usecase

import app.cash.turbine.test
import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.cookingtimer.data.repository.FakeTimerRepository
import com.eslam.bakingapp.features.cookingtimer.domain.model.CookingTimer
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerStatus
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GetTimersUseCase and related use cases.
 */
class GetTimersUseCaseTest {
    
    private lateinit var repository: FakeTimerRepository
    private lateinit var getTimersUseCase: GetTimersUseCase
    private lateinit var getActiveTimersUseCase: GetActiveTimersUseCase
    private lateinit var getTimerByIdUseCase: GetTimerByIdUseCase
    
    @Before
    fun setup() {
        repository = FakeTimerRepository()
        getTimersUseCase = GetTimersUseCase(repository)
        getActiveTimersUseCase = GetActiveTimersUseCase(repository)
        getTimerByIdUseCase = GetTimerByIdUseCase(repository)
    }
    
    @Test
    fun `getTimers returns all timers`() = runTest {
        // Given
        val timer1 = createTimer("1", "Timer 1")
        val timer2 = createTimer("2", "Timer 2")
        repository.addTimer(timer1)
        repository.addTimer(timer2)
        
        // When/Then
        getTimersUseCase().test {
            val result = awaitItem()
            assertThat(result).isInstanceOf(Result.Success::class.java)
            assertThat((result as Result.Success).data).hasSize(2)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `getTimers returns empty list when no timers`() = runTest {
        getTimersUseCase().test {
            val result = awaitItem()
            assertThat(result).isInstanceOf(Result.Success::class.java)
            assertThat((result as Result.Success).data).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `getActiveTimers returns only running and paused timers`() = runTest {
        // Given
        val runningTimer = createTimer("1", "Running", status = TimerStatus.RUNNING)
        val pausedTimer = createTimer("2", "Paused", status = TimerStatus.PAUSED)
        val idleTimer = createTimer("3", "Idle", status = TimerStatus.IDLE)
        val completedTimer = createTimer("4", "Completed", status = TimerStatus.COMPLETED)
        
        repository.addTimer(runningTimer)
        repository.addTimer(pausedTimer)
        repository.addTimer(idleTimer)
        repository.addTimer(completedTimer)
        
        // When/Then
        getActiveTimersUseCase().test {
            val result = awaitItem()
            assertThat(result).isInstanceOf(Result.Success::class.java)
            val timers = (result as Result.Success).data
            assertThat(timers).hasSize(2)
            assertThat(timers.map { it.id }).containsExactly("1", "2")
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `getTimerById returns correct timer`() = runTest {
        // Given
        val timer = createTimer("123", "Test Timer")
        repository.addTimer(timer)
        
        // When
        val result = getTimerByIdUseCase("123")
        
        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data.id).isEqualTo("123")
        assertThat(result.data.name).isEqualTo("Test Timer")
    }
    
    @Test
    fun `getTimerById returns error when timer not found`() = runTest {
        // When
        val result = getTimerByIdUseCase("nonexistent")
        
        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).contains("not found")
    }
    
    @Test
    fun `getTimers returns error when repository fails`() = runTest {
        // Given
        repository.shouldReturnError = true
        repository.errorMessage = "Network error"
        
        // When/Then
        getTimersUseCase().test {
            val result = awaitItem()
            assertThat(result).isInstanceOf(Result.Error::class.java)
            assertThat((result as Result.Error).message).isEqualTo("Network error")
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    private fun createTimer(
        id: String,
        name: String,
        status: TimerStatus = TimerStatus.IDLE
    ) = CookingTimer(
        id = id,
        name = name,
        description = "Test",
        durationSeconds = 300,
        remainingSeconds = 300,
        status = status
    )
}


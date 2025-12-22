package com.eslam.bakingapp.features.cookingtimer.domain.usecase

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.cookingtimer.data.repository.FakeTimerRepository
import com.eslam.bakingapp.features.cookingtimer.domain.model.CookingTimer
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerStatus
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for timer control use cases (Start, Pause, Resume, Stop, Reset, Delete).
 */
class TimerControlUseCaseTest {
    
    private lateinit var repository: FakeTimerRepository
    private lateinit var startTimerUseCase: StartTimerUseCase
    private lateinit var pauseTimerUseCase: PauseTimerUseCase
    private lateinit var resumeTimerUseCase: ResumeTimerUseCase
    private lateinit var stopTimerUseCase: StopTimerUseCase
    private lateinit var resetTimerUseCase: ResetTimerUseCase
    private lateinit var deleteTimerUseCase: DeleteTimerUseCase
    private lateinit var clearCompletedTimersUseCase: ClearCompletedTimersUseCase
    
    @Before
    fun setup() {
        repository = FakeTimerRepository()
        startTimerUseCase = StartTimerUseCase(repository)
        pauseTimerUseCase = PauseTimerUseCase(repository)
        resumeTimerUseCase = ResumeTimerUseCase(repository)
        stopTimerUseCase = StopTimerUseCase(repository)
        resetTimerUseCase = ResetTimerUseCase(repository)
        deleteTimerUseCase = DeleteTimerUseCase(repository)
        clearCompletedTimersUseCase = ClearCompletedTimersUseCase(repository)
    }
    
    @Test
    fun `startTimer changes status to RUNNING`() = runTest {
        // Given
        val timer = createTimer("1", TimerStatus.IDLE)
        repository.addTimer(timer)
        
        // When
        val result = startTimerUseCase("1")
        
        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val updatedTimer = (repository.getTimerById("1") as Result.Success).data
        assertThat(updatedTimer.status).isEqualTo(TimerStatus.RUNNING)
    }
    
    @Test
    fun `pauseTimer changes status to PAUSED`() = runTest {
        // Given
        val timer = createTimer("1", TimerStatus.RUNNING)
        repository.addTimer(timer)
        
        // When
        val result = pauseTimerUseCase("1")
        
        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val updatedTimer = (repository.getTimerById("1") as Result.Success).data
        assertThat(updatedTimer.status).isEqualTo(TimerStatus.PAUSED)
    }
    
    @Test
    fun `resumeTimer changes status to RUNNING`() = runTest {
        // Given
        val timer = createTimer("1", TimerStatus.PAUSED)
        repository.addTimer(timer)
        
        // When
        val result = resumeTimerUseCase("1")
        
        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val updatedTimer = (repository.getTimerById("1") as Result.Success).data
        assertThat(updatedTimer.status).isEqualTo(TimerStatus.RUNNING)
    }
    
    @Test
    fun `stopTimer changes status to CANCELLED`() = runTest {
        // Given
        val timer = createTimer("1", TimerStatus.RUNNING)
        repository.addTimer(timer)
        
        // When
        val result = stopTimerUseCase("1")
        
        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val updatedTimer = (repository.getTimerById("1") as Result.Success).data
        assertThat(updatedTimer.status).isEqualTo(TimerStatus.CANCELLED)
    }
    
    @Test
    fun `resetTimer resets remaining time and status`() = runTest {
        // Given
        val timer = CookingTimer(
            id = "1",
            name = "Test",
            description = "Test",
            durationSeconds = 300,
            remainingSeconds = 100, // Partially elapsed
            status = TimerStatus.PAUSED
        )
        repository.addTimer(timer)
        
        // When
        val result = resetTimerUseCase("1")
        
        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val updatedTimer = (repository.getTimerById("1") as Result.Success).data
        assertThat(updatedTimer.remainingSeconds).isEqualTo(300) // Reset to original
        assertThat(updatedTimer.status).isEqualTo(TimerStatus.IDLE)
    }
    
    @Test
    fun `deleteTimer removes timer from repository`() = runTest {
        // Given
        val timer = createTimer("1", TimerStatus.IDLE)
        repository.addTimer(timer)
        
        // When
        val result = deleteTimerUseCase("1")
        
        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val getResult = repository.getTimerById("1")
        assertThat(getResult).isInstanceOf(Result.Error::class.java)
    }
    
    @Test
    fun `clearCompletedTimers removes completed and cancelled timers`() = runTest {
        // Given
        repository.addTimer(createTimer("1", TimerStatus.RUNNING))
        repository.addTimer(createTimer("2", TimerStatus.COMPLETED))
        repository.addTimer(createTimer("3", TimerStatus.CANCELLED))
        repository.addTimer(createTimer("4", TimerStatus.IDLE))
        
        // When
        val result = clearCompletedTimersUseCase()
        
        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(2) // 2 cleared
        
        val remaining = (repository.getAllTimers().first() as Result.Success).data
        assertThat(remaining).hasSize(2)
        assertThat(remaining.map { it.id }).containsExactly("1", "4")
    }
    
    @Test
    fun `control operations fail for nonexistent timer`() = runTest {
        val result = startTimerUseCase("nonexistent")
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }
    
    private fun createTimer(
        id: String,
        status: TimerStatus
    ) = CookingTimer(
        id = id,
        name = "Timer $id",
        description = "Test",
        durationSeconds = 300,
        remainingSeconds = 300,
        status = status
    )
}


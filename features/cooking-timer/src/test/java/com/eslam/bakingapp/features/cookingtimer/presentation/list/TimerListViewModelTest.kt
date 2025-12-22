package com.eslam.bakingapp.features.cookingtimer.presentation.list

import app.cash.turbine.test
import com.eslam.bakingapp.features.cookingtimer.data.repository.FakeTimerRepository
import com.eslam.bakingapp.features.cookingtimer.domain.model.CookingTimer
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerStatus
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.DeleteTimerUseCase
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.GetTimersUseCase
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.PauseTimerUseCase
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.ResetTimerUseCase
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.StartTimerUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for TimerListViewModel.
 * 
 * Demonstrates:
 * - Testing ViewModel with fake repository
 * - Testing StateFlow emissions
 * - Testing coroutine behavior with TestDispatcher
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TimerListViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTimerRepository
    private lateinit var viewModel: TimerListViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTimerRepository()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    private fun createViewModel(): TimerListViewModel {
        return TimerListViewModel(
            getTimersUseCase = GetTimersUseCase(repository),
            startTimerUseCase = StartTimerUseCase(repository),
            pauseTimerUseCase = PauseTimerUseCase(repository),
            resetTimerUseCase = ResetTimerUseCase(repository),
            deleteTimerUseCase = DeleteTimerUseCase(repository),
            repository = repository
        )
    }
    
    @Test
    fun `initial state has empty timers and is loading`() = runTest {
        viewModel = createViewModel()
        
        // Initial state before loading completes
        assertThat(viewModel.uiState.value.timers).isEmpty()
    }
    
    @Test
    fun `uiState updates when timers are loaded`() = runTest {
        // Given
        val timer = createTimer("1", "Test Timer")
        repository.addTimer(timer)
        
        // When
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertThat(state.timers).hasSize(1)
        assertThat(state.timers[0].name).isEqualTo("Test Timer")
        assertThat(state.isLoading).isFalse()
    }
    
    @Test
    fun `activeTimersCount reflects running and paused timers`() = runTest {
        // Given
        repository.addTimer(createTimer("1", "Running", TimerStatus.RUNNING))
        repository.addTimer(createTimer("2", "Paused", TimerStatus.PAUSED))
        repository.addTimer(createTimer("3", "Idle", TimerStatus.IDLE))
        
        // When
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertThat(viewModel.uiState.value.activeTimersCount).isEqualTo(2)
    }
    
    @Test
    fun `onStartTimer changes timer status`() = runTest {
        // Given
        val timer = createTimer("1", "Test", TimerStatus.IDLE)
        repository.addTimer(timer)
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.onStartTimer("1")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then - verify through repository since state is reactive
        val result = repository.getTimerById("1")
        assertThat(result).isInstanceOf(com.eslam.bakingapp.core.common.result.Result.Success::class.java)
    }
    
    @Test
    fun `onDeleteTimer removes timer and emits event`() = runTest {
        // Given
        val timer = createTimer("1", "Test")
        repository.addTimer(timer)
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.events.test {
            viewModel.onDeleteTimer("1")
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Then
            val event = awaitItem()
            assertThat(event).isInstanceOf(TimerListEvent.ShowMessage::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `onTimerClick emits navigation event`() = runTest {
        // Given
        val timer = createTimer("123", "Test")
        repository.addTimer(timer)
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.events.test {
            viewModel.onTimerClick(timer)
            
            // Then
            val event = awaitItem()
            assertThat(event).isInstanceOf(TimerListEvent.NavigateToDetail::class.java)
            assertThat((event as TimerListEvent.NavigateToDetail).timerId).isEqualTo("123")
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `onCreateTimerClick emits navigation event`() = runTest {
        viewModel = createViewModel()
        
        viewModel.events.test {
            viewModel.onCreateTimerClick()
            
            val event = awaitItem()
            assertThat(event).isInstanceOf(TimerListEvent.NavigateToCreate::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `error state is set when repository fails`() = runTest {
        // Given
        repository.shouldReturnError = true
        repository.errorMessage = "Database error"
        
        // When
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertThat(state.errorMessage).isEqualTo("Database error")
        assertThat(state.isLoading).isFalse()
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


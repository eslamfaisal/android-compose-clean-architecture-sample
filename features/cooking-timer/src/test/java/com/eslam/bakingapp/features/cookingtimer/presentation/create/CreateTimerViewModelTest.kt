package com.eslam.bakingapp.features.cookingtimer.presentation.create

import app.cash.turbine.test
import com.eslam.bakingapp.features.cookingtimer.data.repository.FakeTimerRepository
import com.eslam.bakingapp.features.cookingtimer.domain.usecase.CreateTimerUseCase
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
 * Unit tests for CreateTimerViewModel.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CreateTimerViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTimerRepository
    private lateinit var viewModel: CreateTimerViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTimerRepository()
        viewModel = CreateTimerViewModel(CreateTimerUseCase(repository))
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state has default values`() {
        val state = viewModel.uiState.value
        
        assertThat(state.name).isEmpty()
        assertThat(state.description).isEmpty()
        assertThat(state.hours).isEqualTo(0)
        assertThat(state.minutes).isEqualTo(5)
        assertThat(state.seconds).isEqualTo(0)
        assertThat(state.isCreating).isFalse()
    }
    
    @Test
    fun `onNameChanged updates state`() {
        viewModel.onNameChanged("Pasta Timer")
        
        assertThat(viewModel.uiState.value.name).isEqualTo("Pasta Timer")
        assertThat(viewModel.uiState.value.nameError).isNull()
    }
    
    @Test
    fun `onNameChanged with blank name sets error`() {
        viewModel.onNameChanged("   ")
        
        assertThat(viewModel.uiState.value.nameError).isNotNull()
    }
    
    @Test
    fun `onDescriptionChanged updates state`() {
        viewModel.onDescriptionChanged("Cook until done")
        
        assertThat(viewModel.uiState.value.description).isEqualTo("Cook until done")
    }
    
    @Test
    fun `onHoursChanged updates state with valid value`() {
        viewModel.onHoursChanged(2)
        
        assertThat(viewModel.uiState.value.hours).isEqualTo(2)
    }
    
    @Test
    fun `onHoursChanged coerces value to valid range`() {
        viewModel.onHoursChanged(100)
        
        assertThat(viewModel.uiState.value.hours).isEqualTo(23) // Max
    }
    
    @Test
    fun `onMinutesChanged updates state`() {
        viewModel.onMinutesChanged(30)
        
        assertThat(viewModel.uiState.value.minutes).isEqualTo(30)
    }
    
    @Test
    fun `onSecondsChanged updates state`() {
        viewModel.onSecondsChanged(45)
        
        assertThat(viewModel.uiState.value.seconds).isEqualTo(45)
    }
    
    @Test
    fun `totalSeconds calculated correctly`() {
        viewModel.onHoursChanged(1)
        viewModel.onMinutesChanged(30)
        viewModel.onSecondsChanged(15)
        
        // 1 hour + 30 minutes + 15 seconds = 3600 + 1800 + 15 = 5415
        assertThat(viewModel.uiState.value.totalSeconds).isEqualTo(5415)
    }
    
    @Test
    fun `isValid returns true when name and duration are valid`() {
        viewModel.onNameChanged("Test Timer")
        viewModel.onMinutesChanged(5)
        
        assertThat(viewModel.uiState.value.isValid).isTrue()
    }
    
    @Test
    fun `isValid returns false when name is blank`() {
        viewModel.onNameChanged("")
        viewModel.onMinutesChanged(5)
        
        assertThat(viewModel.uiState.value.isValid).isFalse()
    }
    
    @Test
    fun `isValid returns false when duration is zero`() {
        viewModel.onNameChanged("Test Timer")
        viewModel.onHoursChanged(0)
        viewModel.onMinutesChanged(0)
        viewModel.onSecondsChanged(0)
        
        assertThat(viewModel.uiState.value.isValid).isFalse()
    }
    
    @Test
    fun `onCreateTimer with valid input emits success events`() = runTest {
        viewModel.onNameChanged("Test Timer")
        viewModel.onMinutesChanged(5)
        
        viewModel.events.test {
            viewModel.onCreateTimer()
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Should emit TimerCreated, ShowMessage, and NavigateBack
            val event1 = awaitItem()
            assertThat(event1).isInstanceOf(CreateTimerEvent.TimerCreated::class.java)
            
            val event2 = awaitItem()
            assertThat(event2).isInstanceOf(CreateTimerEvent.ShowMessage::class.java)
            
            val event3 = awaitItem()
            assertThat(event3).isInstanceOf(CreateTimerEvent.NavigateBack::class.java)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `onCreateTimer with blank name sets error`() = runTest {
        viewModel.onNameChanged("")
        viewModel.onMinutesChanged(5)
        
        viewModel.onCreateTimer()
        
        assertThat(viewModel.uiState.value.nameError).isNotNull()
    }
    
    @Test
    fun `onCancelClick emits NavigateBack event`() = runTest {
        viewModel.events.test {
            viewModel.onCancelClick()
            
            val event = awaitItem()
            assertThat(event).isInstanceOf(CreateTimerEvent.NavigateBack::class.java)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `formattedDuration shows correct format`() {
        viewModel.onHoursChanged(1)
        viewModel.onMinutesChanged(5)
        viewModel.onSecondsChanged(30)
        
        assertThat(viewModel.uiState.value.formattedDuration).isEqualTo("01:05:30")
    }
    
    @Test
    fun `formattedDuration without hours shows MM-SS format`() {
        viewModel.onHoursChanged(0)
        viewModel.onMinutesChanged(5)
        viewModel.onSecondsChanged(30)
        
        assertThat(viewModel.uiState.value.formattedDuration).isEqualTo("05:30")
    }
}


package com.eslam.bakingapp.features.cookingtimer.domain.usecase

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.cookingtimer.data.repository.FakeTimerRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for CreateTimerUseCase.
 */
class CreateTimerUseCaseTest {
    
    private lateinit var repository: FakeTimerRepository
    private lateinit var useCase: CreateTimerUseCase
    
    @Before
    fun setup() {
        repository = FakeTimerRepository()
        useCase = CreateTimerUseCase(repository)
    }
    
    @Test
    fun `invoke with valid input creates timer successfully`() = runTest {
        val result = useCase(
            name = "Pasta Timer",
            description = "Cook until al dente",
            durationSeconds = 480
        )
        
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val timer = (result as Result.Success).data
        assertThat(timer.name).isEqualTo("Pasta Timer")
        assertThat(timer.description).isEqualTo("Cook until al dente")
        assertThat(timer.durationSeconds).isEqualTo(480)
    }
    
    @Test
    fun `invoke with blank name returns error`() = runTest {
        val result = useCase(
            name = "   ",
            description = "Test",
            durationSeconds = 60
        )
        
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).contains("empty")
    }
    
    @Test
    fun `invoke with zero duration returns error`() = runTest {
        val result = useCase(
            name = "Test Timer",
            description = "Test",
            durationSeconds = 0
        )
        
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).contains("greater than 0")
    }
    
    @Test
    fun `invoke with negative duration returns error`() = runTest {
        val result = useCase(
            name = "Test Timer",
            description = "Test",
            durationSeconds = -60
        )
        
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }
    
    @Test
    fun `invoke with duration over 24 hours returns error`() = runTest {
        val result = useCase(
            name = "Test Timer",
            description = "Test",
            durationSeconds = 25 * 60 * 60 // 25 hours
        )
        
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).contains("24 hours")
    }
    
    @Test
    fun `invoke trims name and description`() = runTest {
        val result = useCase(
            name = "  Pasta Timer  ",
            description = "  Cook until done  ",
            durationSeconds = 60
        )
        
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val timer = (result as Result.Success).data
        assertThat(timer.name).isEqualTo("Pasta Timer")
        assertThat(timer.description).isEqualTo("Cook until done")
    }
    
    @Test
    fun `invoke when repository fails returns error`() = runTest {
        repository.shouldReturnError = true
        repository.errorMessage = "Database error"
        
        val result = useCase(
            name = "Test Timer",
            description = "Test",
            durationSeconds = 60
        )
        
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }
}


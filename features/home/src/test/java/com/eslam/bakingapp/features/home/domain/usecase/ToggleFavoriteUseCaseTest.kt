package com.eslam.bakingapp.features.home.domain.usecase

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.home.data.repository.FakeRecipeRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ToggleFavoriteUseCaseTest {
    
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    private lateinit var fakeRepository: FakeRecipeRepository
    
    @Before
    fun setup() {
        fakeRepository = FakeRecipeRepository()
        toggleFavoriteUseCase = ToggleFavoriteUseCase(fakeRepository)
    }
    
    @Test
    fun `toggle favorite for existing recipe returns Success`() = runTest {
        val result = toggleFavoriteUseCase("1")
        assertThat(result).isInstanceOf(Result.Success::class.java)
    }
    
    @Test
    fun `toggle favorite for non-existing recipe returns Error`() = runTest {
        val result = toggleFavoriteUseCase("non-existent-id")
        
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo("Recipe not found")
    }
    
    @Test
    fun `toggle favorite returns Error when repository fails`() = runTest {
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Database error"
        
        val result = toggleFavoriteUseCase("1")
        
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo("Database error")
    }
}





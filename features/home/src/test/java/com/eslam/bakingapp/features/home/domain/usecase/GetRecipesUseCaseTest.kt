package com.eslam.bakingapp.features.home.domain.usecase

import app.cash.turbine.test
import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.home.data.repository.FakeRecipeRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetRecipesUseCaseTest {
    
    private lateinit var getRecipesUseCase: GetRecipesUseCase
    private lateinit var fakeRepository: FakeRecipeRepository
    
    @Before
    fun setup() {
        fakeRepository = FakeRecipeRepository()
        getRecipesUseCase = GetRecipesUseCase(fakeRepository)
    }
    
    @Test
    fun `invoke returns Loading then Success with recipes`() = runTest {
        getRecipesUseCase().test {
            // First emission should be Loading
            val loading = awaitItem()
            assertThat(loading).isEqualTo(Result.Loading)
            
            // Second emission should be Success with data
            val success = awaitItem()
            assertThat(success).isInstanceOf(Result.Success::class.java)
            assertThat((success as Result.Success).data).hasSize(3)
            
            awaitComplete()
        }
    }
    
    @Test
    fun `invoke returns Error when repository fails`() = runTest {
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Network error"
        
        getRecipesUseCase().test {
            // First emission should be Loading
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            
            // Second emission should be Error
            val error = awaitItem()
            assertThat(error).isInstanceOf(Result.Error::class.java)
            assertThat((error as Result.Error).message).isEqualTo("Network error")
            
            awaitComplete()
        }
    }
    
    @Test
    fun `invoke returns empty list when no recipes`() = runTest {
        fakeRepository.clearRecipes()
        
        getRecipesUseCase().test {
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            
            val success = awaitItem()
            assertThat(success).isInstanceOf(Result.Success::class.java)
            assertThat((success as Result.Success).data).isEmpty()
            
            awaitComplete()
        }
    }
}





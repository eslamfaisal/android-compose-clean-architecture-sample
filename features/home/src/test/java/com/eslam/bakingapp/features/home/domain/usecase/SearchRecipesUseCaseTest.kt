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
class SearchRecipesUseCaseTest {
    
    private lateinit var searchRecipesUseCase: SearchRecipesUseCase
    private lateinit var fakeRepository: FakeRecipeRepository
    
    @Before
    fun setup() {
        fakeRepository = FakeRecipeRepository()
        searchRecipesUseCase = SearchRecipesUseCase(fakeRepository)
    }
    
    @Test
    fun `search with matching query returns filtered results`() = runTest {
        searchRecipesUseCase("Chocolate").test {
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            
            val success = awaitItem()
            assertThat(success).isInstanceOf(Result.Success::class.java)
            val recipes = (success as Result.Success).data
            assertThat(recipes).hasSize(1)
            assertThat(recipes.first().name).contains("Chocolate")
            
            awaitComplete()
        }
    }
    
    @Test
    fun `search with no matching query returns empty list`() = runTest {
        searchRecipesUseCase("NonExistent").test {
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            
            val success = awaitItem()
            assertThat(success).isInstanceOf(Result.Success::class.java)
            assertThat((success as Result.Success).data).isEmpty()
            
            awaitComplete()
        }
    }
    
    @Test
    fun `search is case insensitive`() = runTest {
        searchRecipesUseCase("chocolate").test {
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            
            val success = awaitItem()
            assertThat(success).isInstanceOf(Result.Success::class.java)
            assertThat((success as Result.Success).data).hasSize(1)
            
            awaitComplete()
        }
    }
    
    @Test
    fun `search with empty query returns all recipes`() = runTest {
        searchRecipesUseCase("").test {
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            
            val success = awaitItem()
            assertThat(success).isInstanceOf(Result.Success::class.java)
            assertThat((success as Result.Success).data).hasSize(3)
            
            awaitComplete()
        }
    }
    
    @Test
    fun `search trims whitespace from query`() = runTest {
        searchRecipesUseCase("  Chocolate  ").test {
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            
            val success = awaitItem()
            assertThat(success).isInstanceOf(Result.Success::class.java)
            assertThat((success as Result.Success).data).hasSize(1)
            
            awaitComplete()
        }
    }
    
    @Test
    fun `search returns error when repository fails`() = runTest {
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Search failed"
        
        searchRecipesUseCase("Chocolate").test {
            assertThat(awaitItem()).isEqualTo(Result.Loading)
            
            val error = awaitItem()
            assertThat(error).isInstanceOf(Result.Error::class.java)
            assertThat((error as Result.Error).message).isEqualTo("Search failed")
            
            awaitComplete()
        }
    }
}





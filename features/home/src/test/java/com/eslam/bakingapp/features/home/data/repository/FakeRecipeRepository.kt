package com.eslam.bakingapp.features.home.data.repository

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.home.domain.model.Difficulty
import com.eslam.bakingapp.features.home.domain.model.Ingredient
import com.eslam.bakingapp.features.home.domain.model.Recipe
import com.eslam.bakingapp.features.home.domain.model.Step
import com.eslam.bakingapp.features.home.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Fake implementation of RecipeRepository for testing.
 */
class FakeRecipeRepository : RecipeRepository {
    
    var shouldReturnError = false
    var errorMessage = "Test error"
    
    private val recipes = mutableListOf(
        createFakeRecipe("1", "Chocolate Chip Cookies", "Cookies"),
        createFakeRecipe("2", "Red Velvet Cupcakes", "Cupcakes"),
        createFakeRecipe("3", "Banana Bread", "Bread")
    )
    
    override fun getRecipes(): Flow<Result<List<Recipe>>> = flow {
        emit(Result.Loading)
        if (shouldReturnError) {
            emit(Result.Error(Exception(errorMessage), errorMessage))
        } else {
            emit(Result.Success(recipes.toList()))
        }
    }
    
    override fun getRecipeById(id: String): Flow<Result<Recipe>> = flow {
        emit(Result.Loading)
        if (shouldReturnError) {
            emit(Result.Error(Exception(errorMessage), errorMessage))
        } else {
            val recipe = recipes.find { it.id == id }
            if (recipe != null) {
                emit(Result.Success(recipe))
            } else {
                emit(Result.Error(NoSuchElementException("Recipe not found"), "Recipe not found"))
            }
        }
    }
    
    override fun searchRecipes(query: String): Flow<Result<List<Recipe>>> = flow {
        emit(Result.Loading)
        if (shouldReturnError) {
            emit(Result.Error(Exception(errorMessage), errorMessage))
        } else {
            val filtered = recipes.filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.description.contains(query, ignoreCase = true) 
            }
            emit(Result.Success(filtered))
        }
    }
    
    override fun getRecipesByCategory(category: String): Flow<Result<List<Recipe>>> = flow {
        emit(Result.Loading)
        if (shouldReturnError) {
            emit(Result.Error(Exception(errorMessage), errorMessage))
        } else {
            val filtered = recipes.filter { it.category.equals(category, ignoreCase = true) }
            emit(Result.Success(filtered))
        }
    }
    
    override fun getFavoriteRecipes(): Flow<Result<List<Recipe>>> = flow {
        emit(Result.Loading)
        if (shouldReturnError) {
            emit(Result.Error(Exception(errorMessage), errorMessage))
        } else {
            val favorites = recipes.filter { it.isFavorite }
            emit(Result.Success(favorites))
        }
    }
    
    override suspend fun toggleFavorite(recipeId: String): Result<Unit> {
        if (shouldReturnError) {
            return Result.Error(Exception(errorMessage), errorMessage)
        }
        
        val index = recipes.indexOfFirst { it.id == recipeId }
        if (index != -1) {
            recipes[index] = recipes[index].copy(isFavorite = !recipes[index].isFavorite)
            return Result.Success(Unit)
        }
        return Result.Error(NoSuchElementException("Recipe not found"), "Recipe not found")
    }
    
    override suspend fun refreshRecipes(): Result<Unit> {
        if (shouldReturnError) {
            return Result.Error(Exception(errorMessage), errorMessage)
        }
        return Result.Success(Unit)
    }
    
    // Helper methods for testing
    fun addRecipe(recipe: Recipe) {
        recipes.add(recipe)
    }
    
    fun clearRecipes() {
        recipes.clear()
    }
    
    companion object {
        fun createFakeRecipe(
            id: String,
            name: String,
            category: String,
            isFavorite: Boolean = false
        ): Recipe = Recipe(
            id = id,
            name = name,
            description = "A delicious $name recipe",
            imageUrl = "https://example.com/image.jpg",
            servings = 4,
            prepTimeMinutes = 15,
            cookTimeMinutes = 30,
            difficulty = Difficulty.MEDIUM,
            category = category,
            isFavorite = isFavorite,
            ingredients = listOf(
                Ingredient("1", "Flour", 2.0, "cups"),
                Ingredient("2", "Sugar", 1.0, "cup")
            ),
            steps = listOf(
                Step("1", 1, "Mix ingredients", null, null),
                Step("2", 2, "Bake at 350°F", null, null)
            )
        )
    }
}





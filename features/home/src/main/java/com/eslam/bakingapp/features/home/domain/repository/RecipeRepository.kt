package com.eslam.bakingapp.features.home.domain.repository

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.home.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for recipe operations.
 * Defines the contract for data operations without implementation details.
 */
interface RecipeRepository {
    
    /**
     * Get all recipes as a Flow for reactive updates.
     */
    fun getRecipes(): Flow<Result<List<Recipe>>>
    
    /**
     * Get a specific recipe by ID.
     */
    fun getRecipeById(id: String): Flow<Result<Recipe>>
    
    /**
     * Search recipes by query string.
     */
    fun searchRecipes(query: String): Flow<Result<List<Recipe>>>
    
    /**
     * Get recipes by category.
     */
    fun getRecipesByCategory(category: String): Flow<Result<List<Recipe>>>
    
    /**
     * Get favorite recipes.
     */
    fun getFavoriteRecipes(): Flow<Result<List<Recipe>>>
    
    /**
     * Toggle favorite status for a recipe.
     */
    suspend fun toggleFavorite(recipeId: String): Result<Unit>
    
    /**
     * Refresh recipes from network.
     */
    suspend fun refreshRecipes(): Result<Unit>
}





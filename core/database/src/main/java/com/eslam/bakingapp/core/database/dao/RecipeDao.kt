package com.eslam.bakingapp.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.eslam.bakingapp.core.database.entity.IngredientEntity
import com.eslam.bakingapp.core.database.entity.RecipeEntity
import com.eslam.bakingapp.core.database.entity.RecipeWithDetails
import com.eslam.bakingapp.core.database.entity.StepEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Recipe operations.
 * Provides methods for CRUD operations and queries.
 */
@Dao
interface RecipeDao {
    
    // ==================== INSERT ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<StepEntity>)
    
    @Transaction
    suspend fun insertRecipeWithDetails(
        recipe: RecipeEntity,
        ingredients: List<IngredientEntity>,
        steps: List<StepEntity>
    ) {
        insertRecipe(recipe)
        insertIngredients(ingredients)
        insertSteps(steps)
    }
    
    // ==================== QUERY ====================
    
    @Query("SELECT * FROM recipes ORDER BY created_at DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>
    
    @Query("SELECT * FROM recipes ORDER BY created_at DESC")
    fun getRecipesPagingSource(): PagingSource<Int, RecipeEntity>
    
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeById(recipeId: String): Flow<RecipeEntity?>
    
    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeWithDetails(recipeId: String): Flow<RecipeWithDetails?>
    
    @Transaction
    @Query("SELECT * FROM recipes ORDER BY created_at DESC")
    fun getAllRecipesWithDetails(): Flow<List<RecipeWithDetails>>
    
    @Query("SELECT * FROM recipes WHERE category = :category ORDER BY created_at DESC")
    fun getRecipesByCategory(category: String): Flow<List<RecipeEntity>>
    
    @Query("SELECT * FROM recipes WHERE is_favorite = 1 ORDER BY created_at DESC")
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>>
    
    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY created_at DESC")
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>
    
    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun getRecipeCount(): Int
    
    // ==================== UPDATE ====================
    
    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)
    
    @Query("UPDATE recipes SET is_favorite = :isFavorite WHERE id = :recipeId")
    suspend fun updateFavoriteStatus(recipeId: String, isFavorite: Boolean)
    
    // ==================== DELETE ====================
    
    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)
    
    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipeById(recipeId: String)
    
    @Query("DELETE FROM recipes")
    suspend fun deleteAllRecipes()
    
    @Query("DELETE FROM ingredients WHERE recipe_id = :recipeId")
    suspend fun deleteIngredientsByRecipeId(recipeId: String)
    
    @Query("DELETE FROM steps WHERE recipe_id = :recipeId")
    suspend fun deleteStepsByRecipeId(recipeId: String)
    
    @Transaction
    suspend fun deleteRecipeWithDetails(recipeId: String) {
        deleteIngredientsByRecipeId(recipeId)
        deleteStepsByRecipeId(recipeId)
        deleteRecipeById(recipeId)
    }
}





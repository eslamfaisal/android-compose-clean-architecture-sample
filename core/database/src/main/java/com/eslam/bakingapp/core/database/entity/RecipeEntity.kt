package com.eslam.bakingapp.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a Recipe in the local database.
 */
@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    
    @ColumnInfo(name = "servings")
    val servings: Int,
    
    @ColumnInfo(name = "prep_time_minutes")
    val prepTimeMinutes: Int,
    
    @ColumnInfo(name = "cook_time_minutes")
    val cookTimeMinutes: Int,
    
    @ColumnInfo(name = "difficulty")
    val difficulty: String,
    
    @ColumnInfo(name = "category")
    val category: String,
    
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Room Entity representing an Ingredient.
 */
@Entity(
    tableName = "ingredients",
    primaryKeys = ["id", "recipe_id"]
)
data class IngredientEntity(
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "recipe_id")
    val recipeId: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "quantity")
    val quantity: Double,
    
    @ColumnInfo(name = "unit")
    val unit: String
)

/**
 * Room Entity representing a cooking Step.
 */
@Entity(
    tableName = "steps",
    primaryKeys = ["id", "recipe_id"]
)
data class StepEntity(
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "recipe_id")
    val recipeId: String,
    
    @ColumnInfo(name = "order")
    val order: Int,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "video_url")
    val videoUrl: String?,
    
    @ColumnInfo(name = "thumbnail_url")
    val thumbnailUrl: String?
)





package com.eslam.bakingapp.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Data class that combines a Recipe with its Ingredients and Steps.
 * Room uses @Relation to automatically join these tables.
 */
data class RecipeWithDetails(
    @Embedded
    val recipe: RecipeEntity,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "recipe_id"
    )
    val ingredients: List<IngredientEntity>,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "recipe_id"
    )
    val steps: List<StepEntity>
)





package com.eslam.bakingapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eslam.bakingapp.core.database.dao.RecipeDao
import com.eslam.bakingapp.core.database.entity.IngredientEntity
import com.eslam.bakingapp.core.database.entity.RecipeEntity
import com.eslam.bakingapp.core.database.entity.StepEntity

/**
 * Room Database for the BakingApp.
 * 
 * Includes all entities and DAOs.
 * Version should be incremented when schema changes.
 */
@Database(
    entities = [
        RecipeEntity::class,
        IngredientEntity::class,
        StepEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class BakingDatabase : RoomDatabase() {
    
    abstract fun recipeDao(): RecipeDao
    
    companion object {
        const val DATABASE_NAME = "baking_app_database"
    }
}





package com.eslam.bakingapp.core.database.di

import android.content.Context
import androidx.room.Room
import com.eslam.bakingapp.core.database.BakingDatabase
import com.eslam.bakingapp.core.database.dao.RecipeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing database-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): BakingDatabase {
        return Room.databaseBuilder(
            context,
            BakingDatabase::class.java,
            BakingDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRecipeDao(database: BakingDatabase): RecipeDao {
        return database.recipeDao()
    }
}





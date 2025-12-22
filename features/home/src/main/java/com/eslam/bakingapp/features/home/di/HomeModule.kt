package com.eslam.bakingapp.features.home.di

import com.eslam.bakingapp.features.home.data.repository.RecipeRepositoryImpl
import com.eslam.bakingapp.features.home.domain.repository.RecipeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for home feature dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {
    
    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        impl: RecipeRepositoryImpl
    ): RecipeRepository
}





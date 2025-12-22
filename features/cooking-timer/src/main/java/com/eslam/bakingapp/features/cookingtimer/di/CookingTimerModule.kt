package com.eslam.bakingapp.features.cookingtimer.di

import com.eslam.bakingapp.features.cookingtimer.data.datasource.LocalTimerDataSource
import com.eslam.bakingapp.features.cookingtimer.data.repository.TimerRepositoryImpl
import com.eslam.bakingapp.features.cookingtimer.domain.repository.TimerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt DI Module for the Cooking Timer feature.
 * 
 * This module demonstrates:
 * - @Binds for interface binding
 * - @Provides for object creation
 * - Singleton scope for shared instances
 * - Feature module DI organization
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CookingTimerModule {
    
    /**
     * Binds the TimerRepository interface to its implementation.
     * 
     * Using @Binds is more efficient than @Provides for simple bindings
     * as it doesn't create an additional wrapper method.
     */
    @Binds
    @Singleton
    abstract fun bindTimerRepository(
        impl: TimerRepositoryImpl
    ): TimerRepository
}

/**
 * Provider module for objects that need @Provides.
 */
@Module
@InstallIn(SingletonComponent::class)
object CookingTimerProviderModule {
    
    /**
     * Provides the LocalTimerDataSource as a singleton.
     * 
     * Note: In this case, since LocalTimerDataSource has an @Inject constructor,
     * we could also just annotate the class with @Singleton directly.
     * This is shown here for demonstration purposes.
     */
    @Provides
    @Singleton
    fun provideLocalTimerDataSource(): LocalTimerDataSource {
        return LocalTimerDataSource()
    }
}


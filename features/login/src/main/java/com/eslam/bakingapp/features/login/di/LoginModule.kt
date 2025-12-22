package com.eslam.bakingapp.features.login.di

import com.eslam.bakingapp.features.login.data.repository.AuthRepositoryImpl
import com.eslam.bakingapp.features.login.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for login feature dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LoginModule {
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}





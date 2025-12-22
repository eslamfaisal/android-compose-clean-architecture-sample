package com.eslam.bakingapp.core.network.di

import com.eslam.bakingapp.core.network.BuildConfig
import com.eslam.bakingapp.core.network.adapter.NetworkResponseAdapterFactory
import com.eslam.bakingapp.core.network.interceptor.AuthInterceptor
import com.eslam.bakingapp.core.network.interceptor.NetworkDelayInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module providing network-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        networkDelayInterceptor: NetworkDelayInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(BuildConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(BuildConfig.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(BuildConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(networkDelayInterceptor)
            .addInterceptor(loggingInterceptor)
            // Certificate pinning can be added here for production
            // .certificatePinner(certificatePinner)
            .retryOnConnectionFailure(true)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .build()
    }
}





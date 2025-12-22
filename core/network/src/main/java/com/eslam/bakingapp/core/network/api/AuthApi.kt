package com.eslam.bakingapp.core.network.api

import com.eslam.bakingapp.core.network.model.LoginRequest
import com.eslam.bakingapp.core.network.model.LoginResponse
import com.eslam.bakingapp.core.network.model.NetworkResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit API interface for authentication endpoints.
 */
interface AuthApi {
    
    @POST("auth/login")
    fun login(
        @Body request: LoginRequest,
        @Header("No-Auth") noAuth: String = "true"
    ): Call<NetworkResponse<LoginResponse>>
    
    @POST("auth/refresh")
    fun refreshToken(
        @Body refreshToken: String,
        @Header("No-Auth") noAuth: String = "true"
    ): Call<NetworkResponse<LoginResponse>>
    
    @POST("auth/logout")
    fun logout(): Call<NetworkResponse<Unit>>
}





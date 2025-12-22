package com.eslam.bakingapp.core.network.api

import com.eslam.bakingapp.core.network.model.NetworkResponse
import com.eslam.bakingapp.core.network.model.RecipeDto
import com.eslam.bakingapp.core.network.model.RecipeListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for recipe-related endpoints.
 */
interface RecipesApi {
    
    @GET("recipes")
    fun getRecipes(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Call<NetworkResponse<RecipeListResponse>>
    
    @GET("recipes/{id}")
    fun getRecipeById(
        @Path("id") recipeId: String
    ): Call<NetworkResponse<RecipeDto>>
    
    @GET("recipes/search")
    fun searchRecipes(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Call<NetworkResponse<RecipeListResponse>>
    
    @GET("recipes/category/{category}")
    fun getRecipesByCategory(
        @Path("category") category: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Call<NetworkResponse<RecipeListResponse>>
}





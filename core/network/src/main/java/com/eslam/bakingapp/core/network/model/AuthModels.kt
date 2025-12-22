package com.eslam.bakingapp.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Login request body
 */
@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "email")
    val email: String,
    
    @Json(name = "password")
    val password: String
)

/**
 * Login response from API
 */
@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "user")
    val user: UserDto,
    
    @Json(name = "access_token")
    val accessToken: String,
    
    @Json(name = "refresh_token")
    val refreshToken: String,
    
    @Json(name = "expires_in")
    val expiresIn: Long
)

/**
 * User data transfer object
 */
@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id")
    val id: String,
    
    @Json(name = "email")
    val email: String,
    
    @Json(name = "name")
    val name: String,
    
    @Json(name = "avatar_url")
    val avatarUrl: String?
)





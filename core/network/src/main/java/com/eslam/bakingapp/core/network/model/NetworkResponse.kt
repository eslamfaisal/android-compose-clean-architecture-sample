package com.eslam.bakingapp.core.network.model

/**
 * A sealed class representing the result of a network operation.
 * This provides a type-safe way to handle different network outcomes.
 */
sealed class NetworkResponse<out T> {
    
    /**
     * Represents a successful network response with data
     */
    data class Success<T>(val data: T) : NetworkResponse<T>()
    
    /**
     * Represents an API error (HTTP 4xx, 5xx responses)
     */
    data class ApiError(
        val code: Int,
        val message: String?,
        val body: String? = null
    ) : NetworkResponse<Nothing>()
    
    /**
     * Represents a network error (no internet, timeout, etc.)
     */
    data class NetworkError(
        val error: Throwable
    ) : NetworkResponse<Nothing>()
    
    /**
     * Represents an unknown error
     */
    data class UnknownError(
        val error: Throwable
    ) : NetworkResponse<Nothing>()
}

/**
 * Extension functions for NetworkResponse
 */
inline fun <T, R> NetworkResponse<T>.map(transform: (T) -> R): NetworkResponse<R> {
    return when (this) {
        is NetworkResponse.Success -> NetworkResponse.Success(transform(data))
        is NetworkResponse.ApiError -> this
        is NetworkResponse.NetworkError -> this
        is NetworkResponse.UnknownError -> this
    }
}

inline fun <T> NetworkResponse<T>.onSuccess(action: (T) -> Unit): NetworkResponse<T> {
    if (this is NetworkResponse.Success) {
        action(data)
    }
    return this
}

inline fun <T> NetworkResponse<T>.onError(action: (String) -> Unit): NetworkResponse<T> {
    when (this) {
        is NetworkResponse.ApiError -> action(message ?: "API Error: $code")
        is NetworkResponse.NetworkError -> action(error.message ?: "Network Error")
        is NetworkResponse.UnknownError -> action(error.message ?: "Unknown Error")
        is NetworkResponse.Success -> { /* do nothing */ }
    }
    return this
}

fun <T> NetworkResponse<T>.getOrNull(): T? {
    return when (this) {
        is NetworkResponse.Success -> data
        else -> null
    }
}

fun <T> NetworkResponse<T>.getOrDefault(default: T): T {
    return when (this) {
        is NetworkResponse.Success -> data
        else -> default
    }
}

val <T> NetworkResponse<T>.isSuccess: Boolean
    get() = this is NetworkResponse.Success

val <T> NetworkResponse<T>.isError: Boolean
    get() = this !is NetworkResponse.Success





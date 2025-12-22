package com.eslam.bakingapp.core.common.result

/**
 * A generic sealed class that holds a value with its loading status.
 * This follows the pattern recommended by Google for handling async operations.
 *
 * @param T The type of data being wrapped
 */
sealed interface Result<out T> {
    
    /**
     * Represents a successful result with data
     */
    data class Success<T>(val data: T) : Result<T>
    
    /**
     * Represents a failed result with an exception
     */
    data class Error(
        val exception: Throwable,
        val message: String? = exception.message
    ) : Result<Nothing>
    
    /**
     * Represents a loading state
     */
    data object Loading : Result<Nothing>
}

/**
 * Extension functions for Result
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> this
        is Result.Loading -> this
    }
}

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

inline fun <T> Result<T>.onError(action: (Throwable, String?) -> Unit): Result<T> {
    if (this is Result.Error) {
        action(exception, message)
    }
    return this
}

inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) {
        action()
    }
    return this
}

fun <T> Result<T>.getOrNull(): T? {
    return when (this) {
        is Result.Success -> data
        else -> null
    }
}

fun <T> Result<T>.getOrDefault(default: T): T {
    return when (this) {
        is Result.Success -> data
        else -> default
    }
}

fun <T> Result<T>.getOrThrow(): T {
    return when (this) {
        is Result.Success -> data
        is Result.Error -> throw exception
        is Result.Loading -> throw IllegalStateException("Result is still loading")
    }
}

val <T> Result<T>.isSuccess: Boolean
    get() = this is Result.Success

val <T> Result<T>.isError: Boolean
    get() = this is Result.Error

val <T> Result<T>.isLoading: Boolean
    get() = this is Result.Loading





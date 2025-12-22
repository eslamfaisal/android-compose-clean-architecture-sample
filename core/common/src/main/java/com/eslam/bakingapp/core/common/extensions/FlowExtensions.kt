package com.eslam.bakingapp.core.common.extensions

import com.eslam.bakingapp.core.common.result.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException

/**
 * Converts a Flow<T> to Flow<Result<T>> with automatic error handling
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it)) }
}

/**
 * Retry with exponential backoff strategy
 * 
 * @param maxRetries Maximum number of retry attempts
 * @param initialDelayMs Initial delay in milliseconds
 * @param maxDelayMs Maximum delay in milliseconds
 * @param factor Multiplier for exponential backoff
 * @param retryWhen Predicate to determine if retry should happen
 */
fun <T> Flow<T>.retryWithExponentialBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 1000L,
    maxDelayMs: Long = 30000L,
    factor: Double = 2.0,
    retryWhen: (Throwable) -> Boolean = { it is IOException }
): Flow<T> {
    var currentDelay = initialDelayMs
    return this.retryWhen { cause, attempt ->
        if (attempt < maxRetries && retryWhen(cause)) {
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMs)
            true
        } else {
            false
        }
    }
}

/**
 * Throttle first - emits only the first item within the specified time window
 */
fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = kotlinx.coroutines.flow.flow {
    var lastEmissionTime = 0L
    collect { value ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEmissionTime >= windowDuration) {
            lastEmissionTime = currentTime
            emit(value)
        }
    }
}





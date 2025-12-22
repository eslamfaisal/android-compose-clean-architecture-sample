package com.eslam.bakingapp.core.common.base

import com.eslam.bakingapp.core.common.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Base class for Use Cases that return a single value.
 * Follows the clean architecture principle of single responsibility.
 *
 * @param P The input parameter type
 * @param R The result type
 */
abstract class UseCase<in P, R>(
    private val coroutineDispatcher: CoroutineDispatcher
) {
    /**
     * Executes the use case logic
     */
    suspend operator fun invoke(parameters: P): Result<R> {
        return try {
            withContext(coroutineDispatcher) {
                Result.Success(execute(parameters))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Override this to implement the business logic
     */
    protected abstract suspend fun execute(parameters: P): R
}

/**
 * Base class for Use Cases that return a Flow.
 * Useful for reactive streams and observing data changes.
 *
 * @param P The input parameter type
 * @param R The result type
 */
abstract class FlowUseCase<in P, R>(
    private val coroutineDispatcher: CoroutineDispatcher
) {
    /**
     * Executes the use case and returns a Flow
     */
    operator fun invoke(parameters: P): Flow<Result<R>> {
        return execute(parameters)
            .catch { e -> emit(Result.Error(e as Exception)) }
            .flowOn(coroutineDispatcher)
    }
    
    /**
     * Override this to implement the business logic
     */
    protected abstract fun execute(parameters: P): Flow<Result<R>>
}

/**
 * A use case that doesn't require any parameters
 */
abstract class NoParamUseCase<R>(
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Result<R> {
        return try {
            withContext(coroutineDispatcher) {
                Result.Success(execute())
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    protected abstract suspend fun execute(): R
}

/**
 * A flow use case that doesn't require any parameters
 */
abstract class NoParamFlowUseCase<R>(
    private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(): Flow<Result<R>> {
        return execute()
            .catch { e -> emit(Result.Error(e as Exception)) }
            .flowOn(coroutineDispatcher)
    }
    
    protected abstract fun execute(): Flow<Result<R>>
}





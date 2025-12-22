package com.eslam.bakingapp.core.network.adapter

import com.eslam.bakingapp.core.network.model.NetworkResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * A Retrofit CallAdapter.Factory that creates adapters for NetworkResponse.
 * This allows Retrofit to automatically wrap responses in NetworkResponse.
 */
class NetworkResponseAdapterFactory : CallAdapter.Factory() {
    
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        // Check if the return type is Call<NetworkResponse<T>>
        if (getRawType(returnType) != Call::class.java) {
            return null
        }
        
        val callType = getParameterUpperBound(0, returnType as ParameterizedType)
        
        if (getRawType(callType) != NetworkResponse::class.java) {
            return null
        }
        
        val responseType = getParameterUpperBound(0, callType as ParameterizedType)
        
        val errorBodyConverter = retrofit.nextResponseBodyConverter<Any>(
            null,
            Any::class.java,
            annotations
        )
        
        return NetworkResponseAdapter<Any>(responseType, errorBodyConverter)
    }
}

/**
 * CallAdapter implementation for NetworkResponse
 */
class NetworkResponseAdapter<T>(
    private val responseType: Type,
    private val errorBodyConverter: Converter<ResponseBody, Any>
) : CallAdapter<T, Call<NetworkResponse<T>>> {
    
    override fun responseType(): Type = responseType
    
    override fun adapt(call: Call<T>): Call<NetworkResponse<T>> {
        return NetworkResponseCall(call, errorBodyConverter)
    }
}

/**
 * A Call wrapper that converts Retrofit responses to NetworkResponse
 */
class NetworkResponseCall<T>(
    private val delegate: Call<T>,
    private val errorBodyConverter: Converter<ResponseBody, Any>
) : Call<NetworkResponse<T>> {
    
    override fun enqueue(callback: Callback<NetworkResponse<T>>) {
        delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val networkResponse = handleResponse(response)
                callback.onResponse(this@NetworkResponseCall, Response.success(networkResponse))
            }
            
            override fun onFailure(call: Call<T>, t: Throwable) {
                val networkResponse = when (t) {
                    is IOException -> NetworkResponse.NetworkError(t)
                    else -> NetworkResponse.UnknownError(t)
                }
                callback.onResponse(this@NetworkResponseCall, Response.success(networkResponse))
            }
        })
    }
    
    override fun execute(): Response<NetworkResponse<T>> {
        return try {
            val response = delegate.execute()
            Response.success(handleResponse(response))
        } catch (e: IOException) {
            Response.success(NetworkResponse.NetworkError(e))
        } catch (e: Exception) {
            Response.success(NetworkResponse.UnknownError(e))
        }
    }
    
    private fun handleResponse(response: Response<T>): NetworkResponse<T> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                NetworkResponse.Success(body)
            } else {
                NetworkResponse.ApiError(
                    code = response.code(),
                    message = "Empty response body"
                )
            }
        } else {
            val errorBody = response.errorBody()?.string()
            NetworkResponse.ApiError(
                code = response.code(),
                message = response.message(),
                body = errorBody
            )
        }
    }
    
    override fun clone(): Call<NetworkResponse<T>> = 
        NetworkResponseCall(delegate.clone(), errorBodyConverter)
    
    override fun isExecuted(): Boolean = delegate.isExecuted
    
    override fun cancel() = delegate.cancel()
    
    override fun isCanceled(): Boolean = delegate.isCanceled
    
    override fun request() = delegate.request()
    
    override fun timeout() = delegate.timeout()
}





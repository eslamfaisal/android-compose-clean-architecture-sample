package com.eslam.bakingapp.core.common.result

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ResultTest {
    
    @Test
    fun `Success result contains data`() {
        val data = "test data"
        val result: Result<String> = Result.Success(data)
        
        assertThat(result.isSuccess).isTrue()
        assertThat(result.isError).isFalse()
        assertThat(result.isLoading).isFalse()
        assertThat(result.getOrNull()).isEqualTo(data)
    }
    
    @Test
    fun `Error result contains exception`() {
        val exception = IllegalStateException("Test error")
        val result: Result<String> = Result.Error(exception, "Custom message")
        
        assertThat(result.isSuccess).isFalse()
        assertThat(result.isError).isTrue()
        assertThat(result.isLoading).isFalse()
        assertThat(result.getOrNull()).isNull()
    }
    
    @Test
    fun `Loading result returns null for getOrNull`() {
        val result: Result<String> = Result.Loading
        
        assertThat(result.isSuccess).isFalse()
        assertThat(result.isError).isFalse()
        assertThat(result.isLoading).isTrue()
        assertThat(result.getOrNull()).isNull()
    }
    
    @Test
    fun `map transforms Success data`() {
        val result: Result<Int> = Result.Success(5)
        val mapped = result.map { it * 2 }
        
        assertThat(mapped).isInstanceOf(Result.Success::class.java)
        assertThat((mapped as Result.Success).data).isEqualTo(10)
    }
    
    @Test
    fun `map preserves Error`() {
        val exception = IllegalStateException("Test")
        val result: Result<Int> = Result.Error(exception)
        val mapped = result.map { it * 2 }
        
        assertThat(mapped).isInstanceOf(Result.Error::class.java)
    }
    
    @Test
    fun `getOrDefault returns data for Success`() {
        val result: Result<String> = Result.Success("value")
        assertThat(result.getOrDefault("default")).isEqualTo("value")
    }
    
    @Test
    fun `getOrDefault returns default for Error`() {
        val result: Result<String> = Result.Error(Exception())
        assertThat(result.getOrDefault("default")).isEqualTo("default")
    }
    
    @Test
    fun `getOrThrow returns data for Success`() {
        val result: Result<String> = Result.Success("value")
        assertThat(result.getOrThrow()).isEqualTo("value")
    }
    
    @Test(expected = IllegalStateException::class)
    fun `getOrThrow throws for Error`() {
        val result: Result<String> = Result.Error(IllegalStateException("Test"))
        result.getOrThrow()
    }
    
    @Test
    fun `onSuccess callback is invoked for Success`() {
        var callbackValue: String? = null
        val result: Result<String> = Result.Success("test")
        
        result.onSuccess { callbackValue = it }
        
        assertThat(callbackValue).isEqualTo("test")
    }
    
    @Test
    fun `onSuccess callback is not invoked for Error`() {
        var callbackInvoked = false
        val result: Result<String> = Result.Error(Exception())
        
        result.onSuccess { callbackInvoked = true }
        
        assertThat(callbackInvoked).isFalse()
    }
    
    @Test
    fun `onError callback is invoked for Error`() {
        var errorMessage: String? = null
        val result: Result<String> = Result.Error(Exception(), "Error occurred")
        
        result.onError { _, message -> errorMessage = message }
        
        assertThat(errorMessage).isEqualTo("Error occurred")
    }
}





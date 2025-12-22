package com.eslam.bakingapp.features.login.domain.usecase

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.login.data.repository.FakeAuthRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginUseCaseTest {
    
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var fakeRepository: FakeAuthRepository
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        fakeRepository = FakeAuthRepository()
        loginUseCase = LoginUseCase(fakeRepository, testDispatcher)
    }
    
    @Test
    fun `login with valid credentials returns Success`() = runTest(testDispatcher) {
        val result = loginUseCase("test@example.com", "Password123")
        
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val loginResult = (result as Result.Success).data
        assertThat(loginResult.userId).isEqualTo("user_123")
        assertThat(loginResult.email).isEqualTo("test@example.com")
        assertThat(loginResult.name).isEqualTo("Test User")
    }
    
    @Test
    fun `login with invalid credentials returns Error`() = runTest(testDispatcher) {
        val result = loginUseCase("wrong@example.com", "WrongPassword123")
        
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo("Invalid email or password")
    }
    
    @Test
    fun `login with repository error returns Error`() = runTest(testDispatcher) {
        fakeRepository.shouldReturnError = true
        fakeRepository.errorMessage = "Network error"
        
        val result = loginUseCase("test@example.com", "Password123")
        
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).message).isEqualTo("Network error")
    }
    
    @Test
    fun `login trims email whitespace`() = runTest(testDispatcher) {
        val result = loginUseCase("  test@example.com  ", "Password123")
        
        assertThat(result).isInstanceOf(Result.Success::class.java)
    }
    
    @Test
    fun `successful login sets logged in state`() = runTest(testDispatcher) {
        assertThat(fakeRepository.isLoggedIn()).isFalse()
        
        loginUseCase("test@example.com", "Password123")
        
        assertThat(fakeRepository.isLoggedIn()).isTrue()
    }
}





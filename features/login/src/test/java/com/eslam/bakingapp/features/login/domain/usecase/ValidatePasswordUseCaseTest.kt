package com.eslam.bakingapp.features.login.domain.usecase

import com.eslam.bakingapp.features.login.domain.model.ValidationResult
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class ValidatePasswordUseCaseTest {
    
    private lateinit var validatePasswordUseCase: ValidatePasswordUseCase
    
    @Before
    fun setup() {
        validatePasswordUseCase = ValidatePasswordUseCase()
    }
    
    @Test
    fun `valid password returns Valid result`() {
        val result = validatePasswordUseCase("Password123")
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }
    
    @Test
    fun `password with special characters returns Valid result`() {
        val result = validatePasswordUseCase("Password123!")
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }
    
    @Test
    fun `empty password returns Invalid result`() {
        val result = validatePasswordUseCase("")
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        assertThat((result as ValidationResult.Invalid).message)
            .isEqualTo("Password cannot be empty")
    }
    
    @Test
    fun `blank password returns Invalid result`() {
        val result = validatePasswordUseCase("   ")
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        assertThat((result as ValidationResult.Invalid).message)
            .isEqualTo("Password cannot be empty")
    }
    
    @Test
    fun `password shorter than 8 characters returns Invalid result`() {
        val result = validatePasswordUseCase("Pass1")
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        assertThat((result as ValidationResult.Invalid).message)
            .isEqualTo("Password must be at least 8 characters")
    }
    
    @Test
    fun `password exactly 8 characters is valid`() {
        val result = validatePasswordUseCase("Passwo1d")
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }
    
    @Test
    fun `password without digit returns Invalid result`() {
        val result = validatePasswordUseCase("PasswordNoDigit")
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        assertThat((result as ValidationResult.Invalid).message)
            .isEqualTo("Password must contain at least one number")
    }
    
    @Test
    fun `password without uppercase returns Invalid result`() {
        val result = validatePasswordUseCase("password123")
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        assertThat((result as ValidationResult.Invalid).message)
            .isEqualTo("Password must contain at least one uppercase letter")
    }
    
    @Test
    fun `password without lowercase returns Invalid result`() {
        val result = validatePasswordUseCase("PASSWORD123")
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        assertThat((result as ValidationResult.Invalid).message)
            .isEqualTo("Password must contain at least one lowercase letter")
    }
    
    @Test
    fun `password with only numbers returns Invalid result`() {
        val result = validatePasswordUseCase("12345678")
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
    }
    
    @Test
    fun `complex valid password returns Valid result`() {
        val result = validatePasswordUseCase("MySecure@Password123!")
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }
}





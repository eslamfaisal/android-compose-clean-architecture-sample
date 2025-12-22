package com.eslam.bakingapp.features.login.domain.usecase

import com.eslam.bakingapp.features.login.domain.model.ValidationResult
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class ValidateEmailUseCaseTest {
    
    private lateinit var validateEmailUseCase: ValidateEmailUseCase
    
    @Before
    fun setup() {
        validateEmailUseCase = ValidateEmailUseCase()
    }
    
    @Test
    fun `valid email returns Valid result`() {
        val result = validateEmailUseCase("test@example.com")
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }
    
    @Test
    fun `valid email with subdomain returns Valid result`() {
        val result = validateEmailUseCase("user@mail.example.com")
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }
    
    @Test
    fun `valid email with plus sign returns Valid result`() {
        val result = validateEmailUseCase("user+tag@example.com")
        assertThat(result).isEqualTo(ValidationResult.Valid)
    }
    
    @Test
    fun `empty email returns Invalid result`() {
        val result = validateEmailUseCase("")
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        assertThat((result as ValidationResult.Invalid).message)
            .isEqualTo("Email cannot be empty")
    }
    
    @Test
    fun `blank email returns Invalid result`() {
        val result = validateEmailUseCase("   ")
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        assertThat((result as ValidationResult.Invalid).message)
            .isEqualTo("Email cannot be empty")
    }
    
    @Test
    fun `email without at symbol returns Invalid result`() {
        val result = validateEmailUseCase("testexample.com")
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
        assertThat((result as ValidationResult.Invalid).message)
            .isEqualTo("Please enter a valid email address")
    }
    
    @Test
    fun `email without domain returns Invalid result`() {
        val result = validateEmailUseCase("test@")
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
    }
    
    @Test
    fun `email without local part returns Invalid result`() {
        val result = validateEmailUseCase("@example.com")
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
    }
    
    @Test
    fun `email with multiple at symbols returns Invalid result`() {
        val result = validateEmailUseCase("test@@example.com")
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
    }
    
    @Test
    fun `email with spaces returns Invalid result`() {
        val result = validateEmailUseCase("test @example.com")
        
        assertThat(result).isInstanceOf(ValidationResult.Invalid::class.java)
    }
}





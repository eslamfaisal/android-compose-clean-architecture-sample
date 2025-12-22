package com.eslam.bakingapp.features.home.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RecipeTest {
    
    @Test
    fun `totalTimeMinutes returns sum of prep and cook time`() {
        val recipe = Recipe(
            id = "1",
            name = "Test Recipe",
            description = "Test description",
            imageUrl = null,
            servings = 4,
            prepTimeMinutes = 15,
            cookTimeMinutes = 30,
            difficulty = Difficulty.MEDIUM,
            category = "Test"
        )
        
        assertThat(recipe.totalTimeMinutes).isEqualTo(45)
    }
    
    @Test
    fun `Difficulty fromString converts correctly`() {
        assertThat(Difficulty.fromString("easy")).isEqualTo(Difficulty.EASY)
        assertThat(Difficulty.fromString("EASY")).isEqualTo(Difficulty.EASY)
        assertThat(Difficulty.fromString("medium")).isEqualTo(Difficulty.MEDIUM)
        assertThat(Difficulty.fromString("hard")).isEqualTo(Difficulty.HARD)
        assertThat(Difficulty.fromString("unknown")).isEqualTo(Difficulty.MEDIUM) // default
    }
    
    @Test
    fun `Difficulty toDisplayString returns correct string`() {
        assertThat(Difficulty.EASY.toDisplayString()).isEqualTo("Easy")
        assertThat(Difficulty.MEDIUM.toDisplayString()).isEqualTo("Medium")
        assertThat(Difficulty.HARD.toDisplayString()).isEqualTo("Hard")
    }
    
    @Test
    fun `Ingredient formatted returns correct string`() {
        val ingredient = Ingredient(
            id = "1",
            name = "Flour",
            quantity = 2.0,
            unit = "cups"
        )
        
        assertThat(ingredient.formatted).isEqualTo("2.0 cups Flour")
    }
    
    @Test
    fun `Recipe with empty ingredients and steps has empty lists`() {
        val recipe = Recipe(
            id = "1",
            name = "Test Recipe",
            description = "Test description",
            imageUrl = null,
            servings = 4,
            prepTimeMinutes = 15,
            cookTimeMinutes = 30,
            difficulty = Difficulty.EASY,
            category = "Test"
        )
        
        assertThat(recipe.ingredients).isEmpty()
        assertThat(recipe.steps).isEmpty()
    }
}





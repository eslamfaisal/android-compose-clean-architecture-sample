package com.eslam.bakingapp.features.recipe_details.presentation

import com.eslam.bakingapp.features.home.domain.model.Recipe

/**
 * UI State for the Recipe Detail screen.
 */
data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTabIndex: Int = 0
) {
    val hasError: Boolean
        get() = errorMessage != null && !isLoading
}

/**
 * Tabs for recipe details.
 */
enum class RecipeDetailTab(val title: String) {
    INGREDIENTS("Ingredients"),
    STEPS("Steps")
}





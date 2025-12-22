package com.eslam.bakingapp.features.home.presentation

import com.eslam.bakingapp.features.home.domain.model.Recipe

/**
 * UI State for the Home screen.
 */
data class HomeUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String? = null
) {
    val isEmpty: Boolean
        get() = recipes.isEmpty() && !isLoading && errorMessage == null
    
    val hasError: Boolean
        get() = errorMessage != null && !isLoading
}

/**
 * Available categories for filtering.
 */
val RecipeCategories = listOf(
    "All",
    "Cookies",
    "Cupcakes",
    "Bread",
    "Tarts",
    "Pastries",
    "Cakes"
)





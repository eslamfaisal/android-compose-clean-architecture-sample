package com.eslam.bakingapp.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.home.domain.usecase.GetRecipesUseCase
import com.eslam.bakingapp.features.home.domain.usecase.SearchRecipesUseCase
import com.eslam.bakingapp.features.home.domain.usecase.ToggleFavoriteUseCase
import com.eslam.metrics.api.MetricsSDK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Home screen.
 * Manages recipe list state and user interactions.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private var searchJob: Job? = null
    
    init {
        loadRecipes()
        // Track screen view
        MetricsSDK.trackAction("home_screen_viewed")
    }
    
    /**
     * Load all recipes.
     */
    fun loadRecipes() {
        viewModelScope.launch {
            getRecipesUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                recipes = result.data,
                                isLoading = false,
                                isRefreshing = false,
                                errorMessage = null
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                isRefreshing = false,
                                errorMessage = result.message ?: "Failed to load recipes"
                            )
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Refresh recipes (pull-to-refresh).
     */
    fun refreshRecipes() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadRecipes()
    }
    
    /**
     * Search recipes with debounce.
     */
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        
        searchJob?.cancel()
        
        if (query.isBlank()) {
            loadRecipes()
            return
        }
        
        searchJob = viewModelScope.launch {
            // Debounce search
            delay(300)
            
            searchRecipesUseCase(query).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Result.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                recipes = result.data,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Select a category filter.
     */
    fun onCategorySelected(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
        
        if (category == null || category == "All") {
            loadRecipes()
        } else {
            // Filter locally for now
            viewModelScope.launch {
                getRecipesUseCase().collect { result ->
                    if (result is Result.Success) {
                        val filtered = result.data.filter { 
                            it.category.equals(category, ignoreCase = true) 
                        }
                        _uiState.update { state ->
                            state.copy(
                                recipes = filtered,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Toggle favorite status for a recipe.
     */
    fun onFavoriteClick(recipeId: String) {
        // Track favorite action with metadata
        MetricsSDK.trackHeavyAction(
            "recipe_favorite_toggled",
            mapOf("recipeId" to recipeId)
        )
        
        viewModelScope.launch {
            // Optimistic update
            _uiState.update { state ->
                state.copy(
                    recipes = state.recipes.map { recipe ->
                        if (recipe.id == recipeId) {
                            recipe.copy(isFavorite = !recipe.isFavorite)
                        } else {
                            recipe
                        }
                    }
                )
            }
            
            // Perform actual update
            when (val result = toggleFavoriteUseCase(recipeId)) {
                is Result.Error -> {
                    // Revert on error
                    _uiState.update { state ->
                        state.copy(
                            recipes = state.recipes.map { recipe ->
                                if (recipe.id == recipeId) {
                                    recipe.copy(isFavorite = !recipe.isFavorite)
                                } else {
                                    recipe
                                }
                            }
                        )
                    }
                }
                else -> { /* Success, already updated */ }
            }
        }
    }
    
    /**
     * Clear error message.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}




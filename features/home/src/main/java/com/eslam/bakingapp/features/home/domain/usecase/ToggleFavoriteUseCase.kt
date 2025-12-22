package com.eslam.bakingapp.features.home.domain.usecase

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.home.domain.repository.RecipeRepository
import javax.inject.Inject

/**
 * Use case for toggling recipe favorite status.
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    /**
     * Toggle favorite status for a recipe.
     */
    suspend operator fun invoke(recipeId: String): Result<Unit> {
        return recipeRepository.toggleFavorite(recipeId)
    }
}





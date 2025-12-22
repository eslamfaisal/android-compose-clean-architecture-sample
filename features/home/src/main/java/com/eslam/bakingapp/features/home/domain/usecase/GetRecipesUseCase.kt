package com.eslam.bakingapp.features.home.domain.usecase

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.home.domain.model.Recipe
import com.eslam.bakingapp.features.home.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting recipes.
 * Encapsulates the business logic for fetching recipes.
 */
class GetRecipesUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    /**
     * Get all recipes.
     */
    operator fun invoke(): Flow<Result<List<Recipe>>> {
        return recipeRepository.getRecipes()
    }
}





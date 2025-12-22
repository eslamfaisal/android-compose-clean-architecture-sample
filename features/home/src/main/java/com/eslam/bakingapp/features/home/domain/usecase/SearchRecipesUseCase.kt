package com.eslam.bakingapp.features.home.domain.usecase

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.home.domain.model.Recipe
import com.eslam.bakingapp.features.home.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for searching recipes.
 */
class SearchRecipesUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    /**
     * Search recipes by query string.
     */
    operator fun invoke(query: String): Flow<Result<List<Recipe>>> {
        return recipeRepository.searchRecipes(query.trim())
    }
}





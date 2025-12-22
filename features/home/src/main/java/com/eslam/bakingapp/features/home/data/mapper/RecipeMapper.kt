package com.eslam.bakingapp.features.home.data.mapper

import com.eslam.bakingapp.core.database.entity.IngredientEntity
import com.eslam.bakingapp.core.database.entity.RecipeEntity
import com.eslam.bakingapp.core.database.entity.RecipeWithDetails
import com.eslam.bakingapp.core.database.entity.StepEntity
import com.eslam.bakingapp.core.network.model.IngredientDto
import com.eslam.bakingapp.core.network.model.RecipeDto
import com.eslam.bakingapp.core.network.model.StepDto
import com.eslam.bakingapp.features.home.domain.model.Difficulty
import com.eslam.bakingapp.features.home.domain.model.Ingredient
import com.eslam.bakingapp.features.home.domain.model.Recipe
import com.eslam.bakingapp.features.home.domain.model.Step

/**
 * Mappers for converting between data layer models and domain models.
 * Following the mapper pattern for clean separation of concerns.
 */

// ==================== DTO to Domain ====================

fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        id = id,
        name = name,
        description = description,
        imageUrl = imageUrl,
        servings = servings,
        prepTimeMinutes = prepTimeMinutes,
        cookTimeMinutes = cookTimeMinutes,
        difficulty = Difficulty.fromString(difficulty),
        category = category,
        ingredients = ingredients.map { it.toDomain() },
        steps = steps.map { it.toDomain() }
    )
}

fun IngredientDto.toDomain(): Ingredient {
    return Ingredient(
        id = id,
        name = name,
        quantity = quantity,
        unit = unit
    )
}

fun StepDto.toDomain(): Step {
    return Step(
        id = id,
        order = order,
        description = description,
        videoUrl = videoUrl,
        thumbnailUrl = thumbnailUrl
    )
}

// ==================== Entity to Domain ====================

fun RecipeEntity.toDomain(): Recipe {
    return Recipe(
        id = id,
        name = name,
        description = description,
        imageUrl = imageUrl,
        servings = servings,
        prepTimeMinutes = prepTimeMinutes,
        cookTimeMinutes = cookTimeMinutes,
        difficulty = Difficulty.fromString(difficulty),
        category = category,
        isFavorite = isFavorite
    )
}

fun RecipeWithDetails.toDomain(): Recipe {
    return Recipe(
        id = recipe.id,
        name = recipe.name,
        description = recipe.description,
        imageUrl = recipe.imageUrl,
        servings = recipe.servings,
        prepTimeMinutes = recipe.prepTimeMinutes,
        cookTimeMinutes = recipe.cookTimeMinutes,
        difficulty = Difficulty.fromString(recipe.difficulty),
        category = recipe.category,
        isFavorite = recipe.isFavorite,
        ingredients = ingredients.map { it.toDomain() },
        steps = steps.sortedBy { it.order }.map { it.toDomain() }
    )
}

fun IngredientEntity.toDomain(): Ingredient {
    return Ingredient(
        id = id,
        name = name,
        quantity = quantity,
        unit = unit
    )
}

fun StepEntity.toDomain(): Step {
    return Step(
        id = id,
        order = order,
        description = description,
        videoUrl = videoUrl,
        thumbnailUrl = thumbnailUrl
    )
}

// ==================== DTO to Entity ====================

fun RecipeDto.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = id,
        name = name,
        description = description,
        imageUrl = imageUrl,
        servings = servings,
        prepTimeMinutes = prepTimeMinutes,
        cookTimeMinutes = cookTimeMinutes,
        difficulty = difficulty,
        category = category
    )
}

fun IngredientDto.toEntity(recipeId: String): IngredientEntity {
    return IngredientEntity(
        id = id,
        recipeId = recipeId,
        name = name,
        quantity = quantity,
        unit = unit
    )
}

fun StepDto.toEntity(recipeId: String): StepEntity {
    return StepEntity(
        id = id,
        recipeId = recipeId,
        order = order,
        description = description,
        videoUrl = videoUrl,
        thumbnailUrl = thumbnailUrl
    )
}





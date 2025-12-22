package com.eslam.bakingapp.features.home.domain.model

/**
 * Domain model representing a Recipe.
 * This is a clean domain model without any framework dependencies.
 */
data class Recipe(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val servings: Int,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val difficulty: Difficulty,
    val category: String,
    val isFavorite: Boolean = false,
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<Step> = emptyList()
) {
    /**
     * Total cooking time including prep
     */
    val totalTimeMinutes: Int
        get() = prepTimeMinutes + cookTimeMinutes
}

/**
 * Domain model for an ingredient.
 */
data class Ingredient(
    val id: String,
    val name: String,
    val quantity: Double,
    val unit: String
) {
    /**
     * Formatted string representation
     */
    val formatted: String
        get() = "$quantity $unit $name"
}

/**
 * Domain model for a cooking step.
 */
data class Step(
    val id: String,
    val order: Int,
    val description: String,
    val videoUrl: String?,
    val thumbnailUrl: String?
)

/**
 * Recipe difficulty levels.
 */
enum class Difficulty {
    EASY,
    MEDIUM,
    HARD;
    
    companion object {
        fun fromString(value: String): Difficulty {
            return when (value.lowercase()) {
                "easy" -> EASY
                "medium" -> MEDIUM
                "hard" -> HARD
                else -> MEDIUM
            }
        }
    }
    
    fun toDisplayString(): String {
        return when (this) {
            EASY -> "Easy"
            MEDIUM -> "Medium"
            HARD -> "Hard"
        }
    }
}





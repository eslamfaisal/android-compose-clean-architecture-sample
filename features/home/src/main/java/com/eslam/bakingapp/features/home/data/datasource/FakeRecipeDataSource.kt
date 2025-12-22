package com.eslam.bakingapp.features.home.data.datasource

import com.eslam.bakingapp.features.home.domain.model.Difficulty
import com.eslam.bakingapp.features.home.domain.model.Ingredient
import com.eslam.bakingapp.features.home.domain.model.Recipe
import com.eslam.bakingapp.features.home.domain.model.Step
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fake data source for providing mock recipe data.
 * Used for demonstration and testing purposes.
 */
@Singleton
class FakeRecipeDataSource @Inject constructor() {
    
    /**
     * Simulates network delay and returns fake recipes.
     */
    suspend fun getFakeRecipes(): List<Recipe> {
        delay(1000) // Simulate network delay
        return fakeRecipes
    }
    
    /**
     * Get a specific recipe by ID.
     */
    suspend fun getFakeRecipeById(id: String): Recipe? {
        delay(500)
        return fakeRecipes.find { it.id == id }
    }
    
    companion object {
        private val fakeRecipes = listOf(
            Recipe(
                id = "1",
                name = "Classic Chocolate Chip Cookies",
                description = "Crispy on the outside, chewy on the inside. These classic chocolate chip cookies are perfect for any occasion.",
                imageUrl = "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=800",
                servings = 24,
                prepTimeMinutes = 15,
                cookTimeMinutes = 12,
                difficulty = Difficulty.EASY,
                category = "Cookies",
                ingredients = listOf(
                    Ingredient("1", "All-purpose flour", 2.25, "cups"),
                    Ingredient("2", "Butter", 1.0, "cup"),
                    Ingredient("3", "Sugar", 0.75, "cup"),
                    Ingredient("4", "Brown sugar", 0.75, "cup"),
                    Ingredient("5", "Eggs", 2.0, "large"),
                    Ingredient("6", "Vanilla extract", 1.0, "tsp"),
                    Ingredient("7", "Baking soda", 1.0, "tsp"),
                    Ingredient("8", "Salt", 1.0, "tsp"),
                    Ingredient("9", "Chocolate chips", 2.0, "cups")
                ),
                steps = listOf(
                    Step("1", 1, "Preheat oven to 375°F (190°C).", null, null),
                    Step("2", 2, "Cream together butter and sugars until light and fluffy.", null, null),
                    Step("3", 3, "Beat in eggs and vanilla.", null, null),
                    Step("4", 4, "Mix in flour, baking soda, and salt.", null, null),
                    Step("5", 5, "Fold in chocolate chips.", null, null),
                    Step("6", 6, "Drop rounded tablespoons onto baking sheets.", null, null),
                    Step("7", 7, "Bake for 9-11 minutes or until golden brown.", null, null)
                )
            ),
            Recipe(
                id = "2",
                name = "Red Velvet Cupcakes",
                description = "Decadent red velvet cupcakes topped with creamy cream cheese frosting. A showstopper at any party!",
                imageUrl = "https://images.unsplash.com/photo-1614707267537-b85aaf00c4b7?w=800",
                servings = 12,
                prepTimeMinutes = 20,
                cookTimeMinutes = 22,
                difficulty = Difficulty.MEDIUM,
                category = "Cupcakes",
                ingredients = listOf(
                    Ingredient("1", "All-purpose flour", 1.5, "cups"),
                    Ingredient("2", "Cocoa powder", 2.0, "tbsp"),
                    Ingredient("3", "Red food coloring", 2.0, "tbsp"),
                    Ingredient("4", "Buttermilk", 1.0, "cup"),
                    Ingredient("5", "Vegetable oil", 0.5, "cup"),
                    Ingredient("6", "Eggs", 2.0, "large"),
                    Ingredient("7", "Vanilla extract", 1.0, "tsp"),
                    Ingredient("8", "Cream cheese", 8.0, "oz"),
                    Ingredient("9", "Powdered sugar", 3.0, "cups")
                ),
                steps = listOf(
                    Step("1", 1, "Preheat oven to 350°F (175°C). Line muffin tin with cupcake liners.", null, null),
                    Step("2", 2, "Mix flour and cocoa powder in a bowl.", null, null),
                    Step("3", 3, "In another bowl, combine buttermilk, oil, eggs, vanilla, and food coloring.", null, null),
                    Step("4", 4, "Gradually add dry ingredients to wet ingredients.", null, null),
                    Step("5", 5, "Fill cupcake liners 2/3 full.", null, null),
                    Step("6", 6, "Bake for 18-22 minutes.", null, null),
                    Step("7", 7, "Make frosting by beating cream cheese and powdered sugar.", null, null),
                    Step("8", 8, "Frost cooled cupcakes.", null, null)
                )
            ),
            Recipe(
                id = "3",
                name = "Sourdough Bread",
                description = "Artisan sourdough bread with a crispy crust and soft, chewy interior. A true baker's masterpiece.",
                imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=800",
                servings = 1,
                prepTimeMinutes = 30,
                cookTimeMinutes = 45,
                difficulty = Difficulty.HARD,
                category = "Bread",
                ingredients = listOf(
                    Ingredient("1", "Bread flour", 4.0, "cups"),
                    Ingredient("2", "Sourdough starter", 1.0, "cup"),
                    Ingredient("3", "Water", 1.5, "cups"),
                    Ingredient("4", "Salt", 2.0, "tsp")
                ),
                steps = listOf(
                    Step("1", 1, "Mix flour, water, and starter. Let rest for 30 minutes (autolyse).", null, null),
                    Step("2", 2, "Add salt and knead for 10 minutes.", null, null),
                    Step("3", 3, "Perform stretch and folds every 30 minutes for 2 hours.", null, null),
                    Step("4", 4, "Bulk ferment for 4-6 hours at room temperature.", null, null),
                    Step("5", 5, "Shape the dough and place in a proofing basket.", null, null),
                    Step("6", 6, "Cold proof in refrigerator overnight.", null, null),
                    Step("7", 7, "Preheat oven with Dutch oven to 500°F (260°C).", null, null),
                    Step("8", 8, "Score dough and bake covered for 20 minutes.", null, null),
                    Step("9", 9, "Remove lid, reduce to 450°F, bake 25 more minutes.", null, null)
                )
            ),
            Recipe(
                id = "4",
                name = "Lemon Tart",
                description = "A zesty lemon tart with a buttery shortcrust pastry and silky smooth lemon curd filling.",
                imageUrl = "https://images.unsplash.com/photo-1519915028121-7d3463d20b13?w=800",
                servings = 8,
                prepTimeMinutes = 40,
                cookTimeMinutes = 35,
                difficulty = Difficulty.MEDIUM,
                category = "Tarts",
                ingredients = listOf(
                    Ingredient("1", "All-purpose flour", 1.5, "cups"),
                    Ingredient("2", "Cold butter", 0.5, "cup"),
                    Ingredient("3", "Sugar", 0.25, "cup"),
                    Ingredient("4", "Egg yolks", 4.0, "large"),
                    Ingredient("5", "Lemon juice", 0.5, "cup"),
                    Ingredient("6", "Lemon zest", 2.0, "tbsp"),
                    Ingredient("7", "Heavy cream", 0.5, "cup")
                ),
                steps = listOf(
                    Step("1", 1, "Make pastry by combining flour, butter, and sugar.", null, null),
                    Step("2", 2, "Press into tart pan and blind bake at 375°F for 15 minutes.", null, null),
                    Step("3", 3, "Whisk egg yolks, sugar, and lemon juice over double boiler.", null, null),
                    Step("4", 4, "Cook until thickened, stirring constantly.", null, null),
                    Step("5", 5, "Remove from heat, add butter and lemon zest.", null, null),
                    Step("6", 6, "Pour filling into baked crust.", null, null),
                    Step("7", 7, "Chill for at least 4 hours before serving.", null, null)
                )
            ),
            Recipe(
                id = "5",
                name = "Cinnamon Rolls",
                description = "Soft, fluffy cinnamon rolls with a gooey cinnamon-sugar filling and cream cheese glaze.",
                imageUrl = "https://images.unsplash.com/photo-1609127102567-8a9a21dc27d8?w=800",
                servings = 12,
                prepTimeMinutes = 30,
                cookTimeMinutes = 25,
                difficulty = Difficulty.MEDIUM,
                category = "Pastries",
                ingredients = listOf(
                    Ingredient("1", "All-purpose flour", 4.0, "cups"),
                    Ingredient("2", "Milk", 1.0, "cup"),
                    Ingredient("3", "Active dry yeast", 2.25, "tsp"),
                    Ingredient("4", "Butter", 0.5, "cup"),
                    Ingredient("5", "Sugar", 0.5, "cup"),
                    Ingredient("6", "Cinnamon", 2.0, "tbsp"),
                    Ingredient("7", "Brown sugar", 1.0, "cup"),
                    Ingredient("8", "Cream cheese", 4.0, "oz"),
                    Ingredient("9", "Powdered sugar", 1.0, "cup")
                ),
                steps = listOf(
                    Step("1", 1, "Warm milk and activate yeast with a pinch of sugar.", null, null),
                    Step("2", 2, "Mix flour, sugar, eggs, and yeast mixture to form dough.", null, null),
                    Step("3", 3, "Knead until smooth, let rise for 1 hour.", null, null),
                    Step("4", 4, "Roll dough into rectangle, spread with butter.", null, null),
                    Step("5", 5, "Sprinkle cinnamon-sugar mixture evenly.", null, null),
                    Step("6", 6, "Roll tightly and cut into 12 pieces.", null, null),
                    Step("7", 7, "Place in greased pan, let rise 30 minutes.", null, null),
                    Step("8", 8, "Bake at 350°F for 25 minutes.", null, null),
                    Step("9", 9, "Make glaze and drizzle over warm rolls.", null, null)
                )
            ),
            Recipe(
                id = "6",
                name = "Banana Bread",
                description = "Moist and tender banana bread made with ripe bananas and a touch of cinnamon. Perfect for breakfast or snacking.",
                imageUrl = "https://images.unsplash.com/photo-1605090930279-dae72a5c7b88?w=800",
                servings = 10,
                prepTimeMinutes = 15,
                cookTimeMinutes = 60,
                difficulty = Difficulty.EASY,
                category = "Bread",
                ingredients = listOf(
                    Ingredient("1", "Ripe bananas", 3.0, "medium"),
                    Ingredient("2", "All-purpose flour", 1.5, "cups"),
                    Ingredient("3", "Sugar", 0.75, "cup"),
                    Ingredient("4", "Butter", 0.33, "cup"),
                    Ingredient("5", "Egg", 1.0, "large"),
                    Ingredient("6", "Baking soda", 1.0, "tsp"),
                    Ingredient("7", "Salt", 0.25, "tsp"),
                    Ingredient("8", "Cinnamon", 0.5, "tsp")
                ),
                steps = listOf(
                    Step("1", 1, "Preheat oven to 350°F (175°C). Grease a 9x5 inch loaf pan.", null, null),
                    Step("2", 2, "Mash bananas in a large bowl.", null, null),
                    Step("3", 3, "Mix in melted butter, sugar, egg, and vanilla.", null, null),
                    Step("4", 4, "Add flour, baking soda, salt, and cinnamon.", null, null),
                    Step("5", 5, "Pour into prepared pan.", null, null),
                    Step("6", 6, "Bake for 55-60 minutes until a toothpick comes out clean.", null, null),
                    Step("7", 7, "Cool in pan for 10 minutes before removing.", null, null)
                )
            )
        )
    }
}





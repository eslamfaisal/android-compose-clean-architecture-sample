package com.eslam.bakingapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.eslam.bakingapp.core.ui.components.ErrorView
import com.eslam.bakingapp.core.ui.components.FullScreenLoading
import com.eslam.bakingapp.core.ui.theme.BakingAppTheme
import com.eslam.bakingapp.features.home.domain.model.Difficulty
import com.eslam.bakingapp.features.home.domain.model.Ingredient
import com.eslam.bakingapp.features.home.domain.model.Recipe
import com.eslam.bakingapp.features.home.domain.model.Step
import com.eslam.bakingapp.features.recipe_details.presentation.RecipeDetailUiState
import org.junit.Rule
import org.junit.Test

class RecipeDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testRecipe = Recipe(
        id = "1",
        name = "Chocolate Chip Cookies",
        description = "Delicious homemade chocolate chip cookies.",
        imageUrl = null,
        servings = 24,
        prepTimeMinutes = 15,
        cookTimeMinutes = 12,
        difficulty = Difficulty.EASY,
        category = "Cookies",
        ingredients = listOf(
            Ingredient("1", "All-purpose flour", 2.25, "cups"),
            Ingredient("2", "Butter", 1.0, "cup")
        ),
        steps = listOf(
            Step("1", 1, "Preheat oven to 375°F.", null, null),
            Step("2", 2, "Mix ingredients.", null, null)
        )
    )

    @Test
    fun recipeDetailScreen_displaysRecipeName() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeDetailTestContent(
                    uiState = RecipeDetailUiState(recipe = testRecipe),
                    onNavigateBack = {},
                    onTabSelected = {},
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Chocolate Chip Cookies").assertIsDisplayed()
    }

    @Test
    fun recipeDetailScreen_displaysRecipeDescription() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeDetailTestContent(
                    uiState = RecipeDetailUiState(recipe = testRecipe),
                    onNavigateBack = {},
                    onTabSelected = {},
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText(testRecipe.description).assertIsDisplayed()
    }

    @Test
    fun recipeDetailScreen_displaysTotalTime() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeDetailTestContent(
                    uiState = RecipeDetailUiState(recipe = testRecipe),
                    onNavigateBack = {},
                    onTabSelected = {},
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("27 min").assertIsDisplayed()
    }

    @Test
    fun recipeDetailScreen_displaysServings() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeDetailTestContent(
                    uiState = RecipeDetailUiState(recipe = testRecipe),
                    onNavigateBack = {},
                    onTabSelected = {},
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("24 servings").assertIsDisplayed()
    }

    @Test
    fun recipeDetailScreen_displaysDifficultyBadge() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeDetailTestContent(
                    uiState = RecipeDetailUiState(recipe = testRecipe),
                    onNavigateBack = {},
                    onTabSelected = {},
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Easy").assertIsDisplayed()
    }

    @Test
    fun recipeDetailScreen_displaysIngredientsTab() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeDetailTestContent(
                    uiState = RecipeDetailUiState(recipe = testRecipe),
                    onNavigateBack = {},
                    onTabSelected = {},
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Ingredients").assertIsDisplayed()
    }

    @Test
    fun recipeDetailScreen_displaysStepsTab() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeDetailTestContent(
                    uiState = RecipeDetailUiState(recipe = testRecipe),
                    onNavigateBack = {},
                    onTabSelected = {},
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Steps").assertIsDisplayed()
    }

    @Test
    fun recipeDetailScreen_displaysIngredients() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeDetailTestContent(
                    uiState = RecipeDetailUiState(recipe = testRecipe, selectedTabIndex = 0),
                    onNavigateBack = {},
                    onTabSelected = {},
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("2.25 cups All-purpose flour").assertIsDisplayed()
    }

    @Test
    fun recipeDetailScreen_backButtonExists() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeDetailTestContent(
                    uiState = RecipeDetailUiState(recipe = testRecipe),
                    onNavigateBack = {},
                    onTabSelected = {},
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun recipeDetailScreen_favoriteButtonExists() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeDetailTestContent(
                    uiState = RecipeDetailUiState(recipe = testRecipe),
                    onNavigateBack = {},
                    onTabSelected = {},
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Favorite").assertIsDisplayed()
    }

    @Test
    fun recipeDetailScreen_showsLoadingState() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeDetailTestContent(
                    uiState = RecipeDetailUiState(isLoading = true),
                    onNavigateBack = {},
                    onTabSelected = {},
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Loading recipe...").assertIsDisplayed()
    }

    @Test
    fun recipeDetailScreen_showsErrorState() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeDetailTestContent(
                    uiState = RecipeDetailUiState(errorMessage = "Recipe not found"),
                    onNavigateBack = {},
                    onTabSelected = {},
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Couldn't load recipe").assertIsDisplayed()
    }

    @Test
    fun recipeDetailScreen_stepsTabClickable() {
        var selectedTab = 0

        composeTestRule.setContent {
            BakingAppTheme {
                RecipeDetailTestContent(
                    uiState = RecipeDetailUiState(
                        recipe = testRecipe,
                        selectedTabIndex = selectedTab
                    ),
                    onNavigateBack = {},
                    onTabSelected = { selectedTab = it },
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Steps").performClick()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeDetailTestContent(
    uiState: RecipeDetailUiState,
    onNavigateBack: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onFavoriteClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.recipe?.name ?: "Recipe Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState.recipe != null) {
                        IconButton(onClick = onFavoriteClick) {
                            Icon(
                                imageVector = if (uiState.recipe?.isFavorite == true) {
                                    Icons.Default.Favorite
                                } else {
                                    Icons.Default.FavoriteBorder
                                },
                                contentDescription = "Favorite"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                FullScreenLoading(
                    message = "Loading recipe...",
                    modifier = Modifier.padding(paddingValues)
                )
            }

            uiState.errorMessage != null -> {
                ErrorView(
                    title = "Couldn't load recipe",
                    message = uiState.errorMessage ?: "Unknown error",
                    modifier = Modifier.padding(paddingValues)
                )
            }

            uiState.recipe != null -> {
                val recipe = uiState.recipe
                LazyColumn(modifier = Modifier.padding(paddingValues)) {
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = recipe?.name ?: "Unknown Recipe",
                                style = MaterialTheme.typography.headlineMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("${recipe?.totalTimeMinutes ?: "Unknown"} min")
                                Text("${recipe?.servings ?: "Unknown"} servings")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            recipe?.difficulty?.toDisplayString()?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = recipe?.description ?: "Unknown Description",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    item {
                        SecondaryTabRow(selectedTabIndex = uiState.selectedTabIndex) {
                            Tab(
                                selected = uiState.selectedTabIndex == 0,
                                onClick = { onTabSelected(0) },
                                text = { Text("Ingredients") }
                            )
                            Tab(
                                selected = uiState.selectedTabIndex == 1,
                                onClick = { onTabSelected(1) },
                                text = { Text("Steps") }
                            )
                        }
                    }

                    when (uiState.selectedTabIndex) {
                        0 -> {
                            items(recipe?.ingredients?.size ?: 0) { index ->
                                recipe?.ingredients[index]?.formatted?.let {
                                    Text(
                                        text = it,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }

                        1 -> {
                            items(recipe?.steps?.size ?: 0) { index ->
                                recipe?.steps[index]?.description?.let {
                                    Text(
                                        text = it,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

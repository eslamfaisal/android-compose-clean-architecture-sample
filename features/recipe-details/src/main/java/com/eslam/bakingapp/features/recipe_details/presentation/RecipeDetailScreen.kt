package com.eslam.bakingapp.features.recipe_details.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.eslam.bakingapp.core.ui.components.ErrorType
import com.eslam.bakingapp.core.ui.components.ErrorView
import com.eslam.bakingapp.core.ui.components.FullScreenLoading
import com.eslam.bakingapp.core.ui.theme.BakingAppTheme
import com.eslam.bakingapp.features.home.domain.model.Difficulty
import com.eslam.bakingapp.features.home.domain.model.Ingredient
import com.eslam.bakingapp.features.home.domain.model.Recipe
import com.eslam.bakingapp.features.home.domain.model.Step

/**
 * Recipe detail screen composable.
 */
@Composable
fun RecipeDetailScreen(
    onNavigateBack: () -> Unit,
    onStartTimer: (recipeName: String, cookingTime: Int) -> Unit = { _, _ -> },
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RecipeDetailContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onTabSelected = viewModel::onTabSelected,
        onFavoriteClick = viewModel::onFavoriteClick,
        onRetry = viewModel::loadRecipeDetails,
        onStartTimer = onStartTimer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeDetailContent(
    uiState: RecipeDetailUiState,
    onNavigateBack: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onFavoriteClick: () -> Unit,
    onRetry: () -> Unit,
    onStartTimer: (recipeName: String, cookingTime: Int) -> Unit = { _, _ -> }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.recipe?.name ?: "Recipe Details",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
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
                        // Timer button
                        IconButton(
                            onClick = {
                                onStartTimer(
                                    uiState.recipe.name,
                                    uiState.recipe.cookTimeMinutes
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Start Timer",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Favorite button
                        IconButton(onClick = onFavoriteClick) {
                            Icon(
                                imageVector = if (uiState.recipe.isFavorite) {
                                    Icons.Default.Favorite
                                } else {
                                    Icons.Default.FavoriteBorder
                                },
                                contentDescription = "Favorite",
                                tint = if (uiState.recipe.isFavorite) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            // FAB to start cooking timer with recipe cook time
            if (uiState.recipe != null) {
                ExtendedFloatingActionButton(
                    onClick = {
                        onStartTimer(
                            uiState.recipe.name,
                            uiState.recipe.cookTimeMinutes
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null
                        )
                    },
                    text = { Text("Start Timer") },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    FullScreenLoading(message = "Loading recipe...")
                }

                uiState.hasError -> {
                    ErrorView(
                        errorType = ErrorType.GENERAL,
                        title = "Couldn't load recipe",
                        message = uiState.errorMessage ?: "Something went wrong",
                        onRetry = onRetry,
                        onSecondaryAction = onNavigateBack,
                        secondaryActionLabel = "Go Back"
                    )
                }

                uiState.recipe != null -> {
                    RecipeDetailBody(
                        recipe = uiState.recipe,
                        selectedTabIndex = uiState.selectedTabIndex,
                        onTabSelected = onTabSelected,
                        onStartTimer = onStartTimer
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipeDetailBody(
    recipe: Recipe,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onStartTimer: (recipeName: String, cookingTime: Int) -> Unit = { _, _ -> }
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header Image
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                AsyncImage(
                    model = recipe.imageUrl,
                    contentDescription = recipe.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                ),
                                startY = 100f
                            )
                        )
                )

                // Recipe info overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoChip(
                            icon = Icons.Default.AccessTime,
                            text = "${recipe.totalTimeMinutes} min"
                        )
                        InfoChip(
                            icon = Icons.Default.People,
                            text = "${recipe.servings} servings"
                        )
                    }
                }

                // Difficulty badge
                DifficultyBadge(
                    difficulty = recipe.difficulty,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )
            }
        }

        // Description
        item {
            Text(
                text = recipe.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Tabs
        item {
            SecondaryTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                RecipeDetailTab.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { onTabSelected(index) },
                        text = { Text(tab.title) }
                    )
                }
            }
        }

        // Tab content
        when (selectedTabIndex) {
            0 -> {
                // Ingredients
                itemsIndexed(recipe.ingredients) { index, ingredient ->
                    IngredientItem(
                        ingredient = ingredient,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    )
                }
            }

            1 -> {
                // Steps
                itemsIndexed(recipe.steps) { index, step ->
                    StepItem(
                        stepNumber = index + 1,
                        step = step,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                        onStartStepTimer = { stepNum ->
                            // Start timer with default 5 minutes for step
                            onStartTimer("${recipe.name} - Step $stepNum", 5)
                        }
                    )
                }
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
    }
}

@Composable
private fun DifficultyBadge(
    difficulty: Difficulty,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (difficulty) {
        Difficulty.EASY -> Color(0xFF4CAF50)
        Difficulty.MEDIUM -> Color(0xFFFFC107)
        Difficulty.HARD -> Color(0xFFFF5722)
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = difficulty.toDisplayString(),
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
    }
}

@Composable
private fun IngredientItem(
    ingredient: Ingredient,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = ingredient.formatted,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun StepItem(
    stepNumber: Int,
    step: Step,
    modifier: Modifier = Modifier,
    onStartStepTimer: ((stepNumber: Int) -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Step number circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = step.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Show timer button for steps that mention time
                if (step.description.contains("minute", ignoreCase = true) ||
                    step.description.contains("hour", ignoreCase = true) ||
                    step.description.contains("bake", ignoreCase = true) ||
                    step.description.contains("cook", ignoreCase = true)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onStartStepTimer?.invoke(stepNumber) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Set Timer",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecipeDetailPreview() {
    BakingAppTheme {
        RecipeDetailContent(
            uiState = RecipeDetailUiState(
                recipe = Recipe(
                    id = "1",
                    name = "Chocolate Chip Cookies",
                    description = "Delicious homemade cookies",
                    imageUrl = null,
                    servings = 24,
                    prepTimeMinutes = 15,
                    cookTimeMinutes = 12,
                    difficulty = Difficulty.EASY,
                    category = "Cookies",
                    ingredients = listOf(
                        Ingredient("1", "Flour", 2.0, "cups"),
                        Ingredient("2", "Sugar", 1.0, "cup")
                    ),
                    steps = listOf(
                        Step("1", 1, "Preheat oven to 350°F", null, null),
                        Step("2", 2, "Bake for 12 minutes until golden", null, null)
                    )
                )
            ),
            onNavigateBack = {},
            onTabSelected = {},
            onFavoriteClick = {},
            onRetry = {},
            onStartTimer = { _, _ -> }
        )
    }
}




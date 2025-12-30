package com.eslam.bakingapp.features.home.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eslam.bakingapp.core.ui.components.BakingTextField
import com.eslam.bakingapp.core.ui.components.ErrorView
import com.eslam.bakingapp.core.ui.components.ErrorType
import com.eslam.bakingapp.core.ui.components.FullScreenLoading
import com.eslam.bakingapp.core.ui.components.RecipeCard
import com.eslam.bakingapp.core.ui.theme.BakingAppTheme
import com.eslam.bakingapp.features.home.domain.model.Difficulty
import com.eslam.bakingapp.features.home.domain.model.Recipe

/**
 * Home screen composable displaying recipe list.
 */
@Composable
fun HomeScreen(
    onRecipeClick: (String) -> Unit,
    onTimerClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    HomeContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onCategorySelected = viewModel::onCategorySelected,
        onRecipeClick = onRecipeClick,
        onFavoriteClick = viewModel::onFavoriteClick,
        onRefresh = viewModel::refreshRecipes,
        onRetry = viewModel::loadRecipes,
        onTimerClick = onTimerClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onRecipeClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onTimerClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "🍰 BakingApp",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                },
                actions = {
                    // Timer button in toolbar
                    IconButton(onClick = onTimerClick) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Cooking Timer",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            // FAB for quick access to timer
            FloatingActionButton(
                onClick = onTimerClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Cooking Timer"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.isLoading && uiState.recipes.isEmpty() -> {
                    FullScreenLoading(message = "Loading delicious recipes...")
                }
                
                uiState.hasError && uiState.recipes.isEmpty() -> {
                    ErrorView(
                        errorType = ErrorType.GENERAL,
                        title = "Couldn't load recipes",
                        message = uiState.errorMessage ?: "Something went wrong",
                        onRetry = onRetry
                    )
                }
                
                else -> {
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = onRefresh
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Search bar
                            item {
                                BakingTextField(
                                    value = uiState.searchQuery,
                                    onValueChange = onSearchQueryChange,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 8.dp),
                                    placeholder = "Search recipes...",
                                    leadingIcon = Icons.Default.Search,
                                    singleLine = true
                                )
                            }
                            
                            // Category chips
                            item {
                                CategoryChips(
                                    categories = RecipeCategories,
                                    selectedCategory = uiState.selectedCategory ?: "All",
                                    onCategorySelected = { category ->
                                        onCategorySelected(if (category == "All") null else category)
                                    }
                                )
                            }
                            
                            // Empty state
                            if (uiState.isEmpty) {
                                item {
                                    EmptyState(
                                        searchQuery = uiState.searchQuery
                                    )
                                }
                            }
                            
                            // Recipe list
                            items(
                                items = uiState.recipes,
                                key = { it.id }
                            ) { recipe ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    RecipeCard(
                                        name = recipe.name,
                                        description = recipe.description,
                                        imageUrl = recipe.imageUrl,
                                        prepTimeMinutes = recipe.prepTimeMinutes,
                                        cookTimeMinutes = recipe.cookTimeMinutes,
                                        servings = recipe.servings,
                                        difficulty = recipe.difficulty.toDisplayString(),
                                        isFavorite = recipe.isFavorite,
                                        onCardClick = { onRecipeClick(recipe.id) },
                                        onFavoriteClick = { onFavoriteClick(recipe.id) },
                                        modifier = Modifier.padding(horizontal = 16.dp)
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

@Composable
private fun CategoryChips(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                label = { Text(category) }
            )
        }
    }
}

@Composable
private fun EmptyState(
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "🍪",
            style = MaterialTheme.typography.displayLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = if (searchQuery.isNotBlank()) {
                "No recipes found for \"$searchQuery\""
            } else {
                "No recipes yet"
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (searchQuery.isNotBlank()) {
                "Try a different search term"
            } else {
                "Pull down to refresh"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    BakingAppTheme {
        HomeContent(
            uiState = HomeUiState(
                recipes = listOf(
                    Recipe(
                        id = "1",
                        name = "Chocolate Chip Cookies",
                        description = "Classic homemade chocolate chip cookies",
                        imageUrl = null,
                        servings = 24,
                        prepTimeMinutes = 15,
                        cookTimeMinutes = 12,
                        difficulty = Difficulty.EASY,
                        category = "Cookies"
                    )
                )
            ),
            onSearchQueryChange = {},
            onCategorySelected = {},
            onRecipeClick = {},
            onFavoriteClick = {},
            onRefresh = {},
            onRetry = {},
            onTimerClick = {}
        )
    }
}




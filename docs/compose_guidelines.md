# 🎨 Jetpack Compose Guidelines

## Overview

BakingApp uses Jetpack Compose with Material 3 for all UI components.

## Project Structure

```
core/ui/
├── src/main/java/com/eslam/bakingapp/core/ui/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Type.kt
│   │   └── Theme.kt
│   └── components/
│       ├── BakingButton.kt
│       ├── BakingTextField.kt
│       ├── LoadingIndicator.kt
│       ├── ErrorView.kt
│       └── RecipeCard.kt
```

## Theming

### Color Scheme

```kotlin
private val LightColorScheme = lightColorScheme(
    primary = Primary,           // #D4654A (Terracotta)
    secondary = Secondary,       // #A67C52 (Brown)
    tertiary = Tertiary,         // #6B8E6B (Sage)
    background = BackgroundLight,
    surface = SurfaceLight,
    error = Error
)

@Composable
fun BakingAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = BakingTypography,
        content = content
    )
}
```

### Typography

```kotlin
val BakingTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Lato,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)
```

## Component Design

### Reusable Button

```kotlin
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        enabled = enabled && !isLoading,
        shape = MaterialTheme.shapes.medium
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}
```

### State Hoisting

```kotlin
// ❌ BAD: State inside composable
@Composable
fun SearchBar() {
    var query by remember { mutableStateOf("") }
    TextField(value = query, onValueChange = { query = it })
}

// ✅ GOOD: State hoisted to caller
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
    )
}
```

## Screen Architecture

```kotlin
@Composable
fun HomeScreen(
    onRecipeClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // 1. Collect state
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // 2. Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is HomeEvent.ShowError -> { /* Show snackbar */ }
            }
        }
    }
    
    // 3. Delegate to stateless content
    HomeContent(
        uiState = uiState,
        onRecipeClick = onRecipeClick,
        onRefresh = viewModel::refreshRecipes
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onRecipeClick: (String) -> Unit,
    onRefresh: () -> Unit
) {
    // Pure UI, no business logic
}
```

## Side Effects

### LaunchedEffect

```kotlin
@Composable
fun RecipeDetailScreen(viewModel: RecipeDetailViewModel) {
    // Runs once when entering composition
    LaunchedEffect(Unit) {
        viewModel.loadRecipe()
    }
    
    // Runs when recipeId changes
    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }
}
```

### rememberCoroutineScope

```kotlin
@Composable
fun HomeScreen() {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    Button(onClick = {
        scope.launch {
            snackbarHostState.showSnackbar("Recipe added!")
        }
    }) {
        Text("Add")
    }
}
```

## Animations

```kotlin
@Composable
fun RecipeCard(isExpanded: Boolean) {
    val cardHeight by animateDpAsState(
        targetValue = if (isExpanded) 300.dp else 150.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )
    
    Card(modifier = Modifier.height(cardHeight)) {
        // Content
    }
}

// Animated visibility
AnimatedVisibility(
    visible = isLoading,
    enter = fadeIn() + slideInVertically(),
    exit = fadeOut() + slideOutVertically()
) {
    LoadingIndicator()
}
```

## Preview Best Practices

```kotlin
@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
private fun RecipeCardPreview() {
    BakingAppTheme {
        RecipeCard(
            recipe = previewRecipe,
            onClick = {}
        )
    }
}

// Preview data
private val previewRecipe = Recipe(
    id = "1",
    name = "Chocolate Chip Cookies",
    description = "Delicious homemade cookies"
)
```

## Navigation

```kotlin
@Composable
fun BakingNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        
        composable(
            route = "recipe/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            RecipeDetailScreen(
                recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            )
        }
    }
}
```





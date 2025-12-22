# ⚡ Performance Optimization

## Overview

BakingApp is optimized for:

- **Smooth UI (60fps)**
- **Minimal battery usage**
- **Efficient memory usage**
- **Fast app startup**

## Compose Performance

### Avoiding Unnecessary Recompositions

```kotlin
// ❌ BAD: Lambda recreated on every recomposition
@Composable
fun RecipeList(recipes: List<Recipe>, onRecipeClick: (String) -> Unit) {
    LazyColumn {
        items(recipes) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { onRecipeClick(recipe.id) } // ❌ New lambda each time
            )
        }
    }
}

// ✅ GOOD: Use remember or stable callbacks
@Composable
fun RecipeList(recipes: List<Recipe>, onRecipeClick: (String) -> Unit) {
    LazyColumn {
        items(
            items = recipes,
            key = { it.id } // ✅ Stable keys
        ) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = remember(recipe.id) { { onRecipeClick(recipe.id) } }
            )
        }
    }
}
```

### Stable Classes

```kotlin
// ✅ Immutable data class - Compose can skip recomposition
@Immutable
data class Recipe(
    val id: String,
    val name: String,
    val description: String
)

// Or mark as Stable if containing mutable references
@Stable
class RecipeState {
    var isFavorite by mutableStateOf(false)
}
```

### derivedStateOf

```kotlin
@Composable
fun SearchResults(query: String, recipes: List<Recipe>) {
    // ✅ Only recalculates when query or recipes change
    val filteredRecipes by remember(query, recipes) {
        derivedStateOf {
            recipes.filter { it.name.contains(query, ignoreCase = true) }
        }
    }
}
```

## State Management

### StateFlow Best Practices

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    // ✅ Use update for atomic state changes
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}
```

### Collecting State Efficiently

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    // ✅ Lifecycle-aware collection
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
}
```

## Memory Management

### Avoiding Memory Leaks

```kotlin
// ❌ BAD: Holding Context reference
class RecipeRepository(private val context: Context)

// ✅ GOOD: Use Application context
class RecipeRepository(@ApplicationContext private val context: Context)
```

### Image Loading

```kotlin
// ✅ Coil handles caching and memory efficiently
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data(recipe.imageUrl)
        .crossfade(true)
        .memoryCacheKey(recipe.id)
        .diskCacheKey(recipe.id)
        .build(),
    contentDescription = recipe.name
)
```

## Coroutine Optimization

### Proper Dispatcher Usage

```kotlin
class RecipeRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    // ✅ Heavy operations on IO dispatcher
    suspend fun loadRecipes() = withContext(ioDispatcher) {
        // Database or network operations
    }
}
```

### Cancellation Support

```kotlin
@HiltViewModel
class SearchViewModel : ViewModel() {
    private var searchJob: Job? = null
    
    fun search(query: String) {
        // ✅ Cancel previous search
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            performSearch(query)
        }
    }
}
```

## Database Optimization

### Efficient Queries

```kotlin
@Dao
interface RecipeDao {
    // ✅ Only select needed columns
    @Query("SELECT id, name, imageUrl FROM recipes")
    fun getRecipePreviews(): Flow<List<RecipePreview>>
    
    // ✅ Use indices for frequently queried columns
    @Query("SELECT * FROM recipes WHERE category = :category")
    fun getByCategory(category: String): Flow<List<RecipeEntity>>
}

// ✅ Add index in entity
@Entity(
    tableName = "recipes",
    indices = [Index(value = ["category"])]
)
data class RecipeEntity(...)
```

## Network Optimization

### Caching Strategy

```kotlin
val okHttpClient = OkHttpClient.Builder()
    .cache(Cache(cacheDir, 10 * 1024 * 1024)) // 10MB cache
    .addNetworkInterceptor { chain ->
        chain.proceed(chain.request()).newBuilder()
            .header("Cache-Control", "max-age=3600")
            .build()
    }
    .build()
```

## Battery Optimization

### WorkManager for Background Work

```kotlin
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: RecipeRepository
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            repository.refreshRecipes()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

// Schedule with constraints
val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.UNMETERED)
    .setRequiresBatteryNotLow(true)
    .build()

WorkManager.getInstance(context)
    .enqueueUniquePeriodicWork(
        "recipe_sync",
        ExistingPeriodicWorkPolicy.KEEP,
        PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
    )
```

## Performance Monitoring

```kotlin
// Use Compose compiler metrics
// In build.gradle.kts:
composeCompiler {
    metricsDestination = layout.buildDirectory.dir("compose_metrics")
    reportsDestination = layout.buildDirectory.dir("compose_reports")
}
```





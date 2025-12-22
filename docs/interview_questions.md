# 🧠 Senior Android Developer Interview Questions

## Based on BakingApp Architecture

---

## 🟦 A. OOP & Clean Architecture

### 1. SOLID Principles

**Q: Explain each SOLID principle and where it's applied in this project.**

**A:**
- **S - Single Responsibility:** Each UseCase handles one operation (LoginUseCase only handles login)
- **O - Open/Closed:** RecipeRepository interface is open for extension (mock implementation for tests)
- **L - Liskov Substitution:** FakeRecipeDataSource can replace real API without breaking functionality
- **I - Interface Segregation:** TokenProvider is a small interface, not the full SecureTokenManager
- **D - Dependency Inversion:** ViewModels depend on UseCase interfaces, not implementations

### 2. Layer Separation

**Q: Why separate Domain, Data, and Presentation layers?**

**A:**
- **Testability:** Domain layer can be tested without Android dependencies
- **Maintainability:** Changes in one layer don't affect others
- **Scalability:** Easy to swap implementations (e.g., different databases)
- **Reusability:** Domain models can be shared across platforms

### 3. Repository Pattern

**Q: What is the difference between Repository vs DataSource?**

**A:**
- **Repository:** Abstraction that coordinates data from multiple sources, implements caching strategy
- **DataSource:** Single source of data (API or Database), no business logic

---

## 🟥 B. Kotlin + Coroutines + Flow

### 1. StateFlow vs SharedFlow

**Q: When would you use StateFlow vs SharedFlow?**

**A:**
```kotlin
// StateFlow: For UI state (always has a value)
private val _uiState = MutableStateFlow(HomeUiState())

// SharedFlow: For events (no initial value, can replay)
private val _events = MutableSharedFlow<LoginEvent>()
```

### 2. GlobalScope

**Q: Why do we avoid GlobalScope?**

**A:**
- No lifecycle awareness
- Cannot be cancelled
- Potential memory leaks
- Use `viewModelScope` or `lifecycleScope` instead

### 3. Structured Concurrency

**Q: Explain the difference between launch, async, and withContext.**

**A:**
```kotlin
// launch: Fire and forget, returns Job
viewModelScope.launch { loadData() }

// async: Returns Deferred, use await() to get result
val deferred = async { fetchUser() }
val user = deferred.await()

// withContext: Changes context, suspends until complete
withContext(Dispatchers.IO) { saveToDatabase() }
```

---

## 🟩 C. Jetpack Compose

### 1. Recomposition

**Q: What causes unnecessary recomposition and how do you avoid it?**

**A:**
- **Causes:** Unstable parameters, lambda recreation, reading state that changes frequently
- **Solutions:** Use `remember`, `key`, `derivedStateOf`, stable classes, `Modifier.key`

### 2. remember vs derivedStateOf

**Q: When to use `remember` vs `derivedStateOf`?**

**A:**
```kotlin
// remember: Cache a value across recompositions
val formatter = remember { DateFormatter() }

// derivedStateOf: Compute derived state only when dependencies change
val filteredList by remember {
    derivedStateOf { list.filter { it.isVisible } }
}
```

### 3. Stable Classes

**Q: What makes a class "stable" in Compose?**

**A:**
- All properties are immutable (`val`)
- All property types are also stable
- Or marked with `@Stable` annotation
- Primitives and `String` are stable by default

---

## 🟧 D. Data Structures & Algorithms

### 1. Paging Complexity

**Q: Explain the time complexity for loading a large dataset with Paging.**

**A:**
- **Loading a page:** O(k) where k is page size
- **Total for n items:** O(n/k) network calls
- **Memory:** O(k * windowSize) - only keeps limited pages in memory

### 2. Debounce Implementation

**Q: Implement a debounce algorithm for search queries.**

**A:**
```kotlin
private var searchJob: Job? = null

fun onSearchQueryChange(query: String) {
    searchJob?.cancel()
    searchJob = viewModelScope.launch {
        delay(300) // Debounce delay
        searchRecipes(query)
    }
}
```

### 3. LRU Cache

**Q: How would you implement LRU caching for images?**

**A:**
```kotlin
// Using LinkedHashMap with access-order
class LRUCache<K, V>(private val maxSize: Int) : LinkedHashMap<K, V>(maxSize, 0.75f, true) {
    override fun removeEldestEntry(eldest: Map.Entry<K, V>): Boolean {
        return size > maxSize
    }
}
// Or use Android's LruCache class
```

---

## 🟪 E. Networking

### 1. Retrofit Stack

**Q: Explain how Retrofit + OkHttp + Moshi work together.**

**A:**
- **Retrofit:** Defines API interfaces with annotations, handles Call adaptation
- **OkHttp:** HTTP client, manages connections, interceptors, caching
- **Moshi:** JSON serialization/deserialization using adapters

### 2. NetworkResponse

**Q: Why use a NetworkResponse sealed class?**

**A:**
- Type-safe error handling
- Compile-time exhaustive checking
- Clear distinction between success, API errors, and network errors
- No null checks needed

### 3. Offline-First

**Q: How does offline-first architecture improve UX?**

**A:**
1. Show cached data immediately (fast perceived performance)
2. Fetch fresh data in background
3. Update UI when new data arrives
4. Handle conflicts gracefully
5. Works without internet

---

## 🟫 F. Security

### 1. EncryptedSharedPreferences

**Q: Why use EncryptedSharedPreferences?**

**A:**
- Data encrypted at rest using AES-256
- Keys stored in Android Keystore
- Protection against device compromise
- Compliance with security standards

### 2. Certificate Pinning

**Q: How does the app defend against MITM attacks?**

**A:**
```kotlin
// Certificate pinning validates server certificate
val pinner = CertificatePinner.Builder()
    .add("api.example.com", "sha256/...")
    .build()
// Prevents interception even with compromised CA
```

---

## 🟨 G. Testing

### 1. Test Types

**Q: Difference between unit test vs instrumentation test.**

**A:**
- **Unit:** JVM only, fast, tests logic (UseCases, ViewModels)
- **Instrumentation:** Runs on device/emulator, tests Android components (Room, UI)

### 2. Testing Coroutines

**Q: How do you test ViewModels with coroutines?**

**A:**
```kotlin
@Test
fun `test loading state`() = runTest {
    val viewModel = HomeViewModel(fakeUseCase)
    
    assertThat(viewModel.uiState.value.isLoading).isFalse()
    
    viewModel.loadRecipes()
    
    advanceUntilIdle()
    
    assertThat(viewModel.uiState.value.isLoading).isFalse()
    assertThat(viewModel.uiState.value.recipes).isNotEmpty()
}
```

---

## 🟧 H. System Design

### 1. Scaling

**Q: How would you scale this app to millions of users?**

**A:**
- Implement pagination for all lists
- Use CDN for images
- Implement aggressive caching
- Add offline sync with conflict resolution
- Use background sync with WorkManager
- Implement analytics for performance monitoring

### 2. Offline-First Design

**Q: Design an offline-first mobile system.**

**A:**
1. **Single Source of Truth:** Room database
2. **Sync Strategy:** WorkManager with constraints
3. **Conflict Resolution:** Last-write-wins or merge
4. **UI Updates:** Flow from Room with LiveData/StateFlow
5. **Network:** Retry with exponential backoff





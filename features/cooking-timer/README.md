# 🍳 Cooking Timer Feature Module

A comprehensive Android feature module demonstrating **Activity with XML layouts**, **Navigation Components**, **Fragment management**, and **proper lifecycle handling**. This module serves as a reference implementation for traditional Android View-based development with modern architecture patterns.

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Module Structure](#module-structure)
3. [Architecture](#architecture)
4. [Features Demonstrated](#features-demonstrated)
5. [Lifecycle Management](#lifecycle-management)
6. [Navigation Component](#navigation-component)
7. [ViewBinding](#viewbinding)
8. [ViewModel & State Management](#viewmodel--state-management)
9. [Dependency Injection](#dependency-injection)
10. [Testing](#testing)
11. [How to Use](#how-to-use)
12. [Resources](#resources)

---

## Overview

The **Cooking Timer** feature allows users to:
- Create cooking timers with custom names and durations
- Start, pause, reset, and delete timers
- Use preset timers for common cooking tasks (boiling eggs, baking cookies, etc.)
- View active timers with real-time countdown updates

This feature is built entirely with **XML layouts** (no Jetpack Compose) to demonstrate traditional Android View system patterns while following modern architecture principles.

---

## Module Structure

```
features/cooking-timer/
├── build.gradle.kts                    # Module dependencies and configuration
├── README.md                           # This documentation
│
├── src/main/
│   ├── AndroidManifest.xml             # Activity declaration and configuration
│   │
│   ├── java/com/eslam/bakingapp/features/cookingtimer/
│   │   │
│   │   ├── data/                       # Data Layer
│   │   │   ├── datasource/
│   │   │   │   └── LocalTimerDataSource.kt    # In-memory data source
│   │   │   └── repository/
│   │   │       └── TimerRepositoryImpl.kt     # Repository implementation
│   │   │
│   │   ├── di/                         # Dependency Injection
│   │   │   └── CookingTimerModule.kt          # Hilt DI module
│   │   │
│   │   ├── domain/                     # Domain Layer
│   │   │   ├── model/
│   │   │   │   └── CookingTimer.kt            # Domain models
│   │   │   ├── repository/
│   │   │   │   └── TimerRepository.kt         # Repository interface
│   │   │   └── usecase/
│   │   │       ├── GetTimersUseCase.kt        # Query use cases
│   │   │       ├── TimerControlUseCase.kt     # Control use cases
│   │   │       └── GetTimerPresetsUseCase.kt  # Presets use case
│   │   │
│   │   └── presentation/               # Presentation Layer
│   │       ├── activity/
│   │       │   └── CookingTimerActivity.kt    # Main Activity
│   │       ├── base/
│   │       │   └── BaseFragment.kt            # Base Fragment class
│   │       ├── list/
│   │       │   ├── TimerListFragment.kt       # Timer list screen
│   │       │   ├── TimerListViewModel.kt      # List ViewModel
│   │       │   ├── TimerListUiState.kt        # UI state
│   │       │   └── adapter/
│   │       │       └── TimerListAdapter.kt    # RecyclerView adapter
│   │       ├── detail/
│   │       │   ├── TimerDetailFragment.kt     # Timer detail screen
│   │       │   ├── TimerDetailViewModel.kt    # Detail ViewModel
│   │       │   └── TimerDetailUiState.kt      # UI state
│   │       ├── create/
│   │       │   ├── CreateTimerFragment.kt     # Create timer screen
│   │       │   ├── CreateTimerViewModel.kt    # Create ViewModel
│   │       │   └── CreateTimerUiState.kt      # UI state
│   │       └── presets/
│   │           ├── TimerPresetsFragment.kt    # Presets screen
│   │           ├── TimerPresetsViewModel.kt   # Presets ViewModel
│   │           ├── TimerPresetsUiState.kt     # UI state
│   │           └── adapter/
│   │               └── PresetsAdapter.kt      # Presets adapter
│   │
│   └── res/
│       ├── layout/                     # XML Layouts
│       │   ├── activity_cooking_timer.xml     # Activity layout
│       │   ├── fragment_timer_list.xml        # List fragment layout
│       │   ├── fragment_timer_detail.xml      # Detail fragment layout
│       │   ├── fragment_create_timer.xml      # Create fragment layout
│       │   ├── fragment_timer_presets.xml     # Presets fragment layout
│       │   ├── item_timer.xml                 # Timer list item
│       │   └── item_preset.xml                # Preset list item
│       │
│       ├── navigation/                 # Navigation Component
│       │   └── cooking_timer_nav_graph.xml    # Navigation graph
│       │
│       ├── anim/                       # Transition Animations
│       │   ├── slide_in_right.xml
│       │   ├── slide_out_left.xml
│       │   ├── slide_in_left.xml
│       │   └── slide_out_right.xml
│       │
│       ├── drawable/                   # Vector Drawables
│       │   ├── ic_add.xml
│       │   ├── ic_play.xml
│       │   ├── ic_pause.xml
│       │   ├── ic_reset.xml
│       │   ├── ic_delete.xml
│       │   ├── ic_error.xml
│       │   ├── ic_timer_empty.xml
│       │   ├── ic_presets.xml
│       │   ├── ic_boiling.xml
│       │   ├── ic_baking.xml
│       │   ├── ic_roasting.xml
│       │   ├── ic_grilling.xml
│       │   ├── ic_simmering.xml
│       │   ├── ic_resting.xml
│       │   ├── bg_badge.xml
│       │   └── bg_circle.xml
│       │
│       ├── menu/
│       │   └── menu_timer_list.xml            # Options menu
│       │
│       └── values/
│           ├── strings.xml                    # String resources
│           ├── colors.xml                     # Color resources
│           └── themes.xml                     # Theme definition
│
└── src/test/                           # Unit Tests
    └── java/com/eslam/bakingapp/features/cookingtimer/
        ├── data/repository/
        │   └── FakeTimerRepository.kt         # Test fake
        ├── domain/
        │   ├── model/
        │   │   └── CookingTimerTest.kt        # Model tests
        │   └── usecase/
        │       ├── CreateTimerUseCaseTest.kt
        │       ├── GetTimersUseCaseTest.kt
        │       └── TimerControlUseCaseTest.kt
        └── presentation/
            ├── list/
            │   └── TimerListViewModelTest.kt
            └── create/
                └── CreateTimerViewModelTest.kt
```

---

## Architecture

This module follows **Clean Architecture** with three distinct layers:

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │  Activity   │  │  Fragments  │  │     ViewModels      │  │
│  │  (XML)      │  │  (XML)      │  │  (StateFlow/Events) │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Models    │  │  Use Cases  │  │ Repository Interface│  │
│  │(CookingTimer│  │ (Business   │  │    (Abstraction)    │  │
│  │ TimerPreset)│  │   Logic)    │  │                     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                       DATA LAYER                             │
│  ┌─────────────────────┐  ┌─────────────────────────────┐   │
│  │ Repository Impl     │  │      Data Sources           │   │
│  │ (TimerRepositoryImpl│  │   (LocalTimerDataSource)    │   │
│  └─────────────────────┘  └─────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Layer Responsibilities

| Layer | Responsibility | Dependencies |
|-------|----------------|--------------|
| **Presentation** | UI, user interactions, state management | Domain |
| **Domain** | Business logic, use cases, models | None (pure Kotlin) |
| **Data** | Data operations, repository impl, data sources | Domain |

---

## Features Demonstrated

### 1. **Activity with XML Layout**
- Traditional Activity setup with `setContentView()`
- `ViewBinding` for type-safe view access
- Toolbar setup with `setSupportActionBar()`
- Navigation Component integration

### 2. **Multiple Fragments**
- Four fragments with different purposes:
  - `TimerListFragment` - Displays all timers
  - `TimerDetailFragment` - Shows timer details
  - `CreateTimerFragment` - Form to create new timer
  - `TimerPresetsFragment` - Preset timer selection

### 3. **Navigation Component**
- Single Activity, multiple Fragments architecture
- Navigation graph with destinations and actions
- Safe Args for type-safe argument passing
- Custom animations for transitions

### 4. **ViewBinding**
- Type-safe view access without `findViewById()`
- Proper lifecycle handling (clearing in `onDestroyView()`)
- Integration with base classes

### 5. **RecyclerView with ListAdapter**
- `DiffUtil` for efficient updates
- Click listeners through constructor
- ViewHolder pattern

### 6. **Material Design 3 Components**
- `MaterialToolbar`
- `MaterialCardView`
- `MaterialButton`
- `TextInputLayout`
- `ChipGroup`
- `FloatingActionButton`
- `CircularProgressIndicator`
- `LinearProgressIndicator`
- `Snackbar`
- `MaterialAlertDialogBuilder`

### 7. **Menu Handling**
- Modern `MenuProvider` API (lifecycle-aware)
- Options menu in toolbar

### 8. **State Management**
- Loading, Error, Empty, Content states
- Proper visibility management

---

## Lifecycle Management

### Activity Lifecycle

The `CookingTimerActivity` demonstrates all Activity lifecycle callbacks:

```kotlin
class CookingTimerActivity : AppCompatActivity() {
    
    // ════════════════════════════════════════════════════════════
    // CREATION PHASE
    // ════════════════════════════════════════════════════════════
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ✓ Initialize ViewBinding
        // ✓ Setup Navigation Component
        // ✓ Register lifecycle observers
        // ✓ Handle saved instance state
    }
    
    override fun onStart() {
        super.onStart()
        // Activity is becoming visible
        // ✓ Start animations
        // ✓ Register broadcast receivers
    }
    
    override fun onResume() {
        super.onResume()
        // Activity is in foreground and interactive
        // ✓ Resume paused operations
        // ✓ Start timer updates
    }
    
    // ════════════════════════════════════════════════════════════
    // DESTRUCTION PHASE
    // ════════════════════════════════════════════════════════════
    
    override fun onPause() {
        super.onPause()
        // Activity losing focus
        // ✓ Pause operations
        // ✓ Commit unsaved changes
    }
    
    override fun onStop() {
        super.onStop()
        // Activity no longer visible
        // ✓ Release resources
        // ✓ Unregister receivers
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Activity being destroyed
        // ✓ Final cleanup
        // ✓ Remove observers
    }
    
    // ════════════════════════════════════════════════════════════
    // STATE MANAGEMENT
    // ════════════════════════════════════════════════════════════
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save UI state before configuration change
    }
    
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore UI state after configuration change
    }
}
```

### Fragment Lifecycle

The `BaseFragment` demonstrates the complete Fragment lifecycle:

```
FRAGMENT LIFECYCLE ORDER:
═══════════════════════════════════════════════════════════════

    CREATION PHASE                 DESTRUCTION PHASE
    ──────────────                 ─────────────────
    
    ┌─────────────┐               ┌─────────────┐
    │  onAttach() │               │  onPause()  │
    │  Fragment   │               │  Losing     │
    │  attached   │               │  focus      │
    └──────┬──────┘               └──────┬──────┘
           │                             │
           ▼                             ▼
    ┌─────────────┐               ┌─────────────┐
    │  onCreate() │               │  onStop()   │
    │  Instance   │               │  Not        │
    │  created    │               │  visible    │
    └──────┬──────┘               └──────┬──────┘
           │                             │
           ▼                             ▼
    ┌──────────────┐              ┌──────────────────┐
    │onCreateView()│              │ onSaveInstance   │
    │ Inflate      │              │ State()          │
    │ layout       │              │ Save UI state    │
    └──────┬───────┘              └──────┬───────────┘
           │                             │
           ▼                             ▼
    ┌──────────────┐              ┌──────────────────┐
    │onViewCreated │              │ onDestroyView()  │
    │ Setup views  │              │ CLEAR BINDING!   │ ← IMPORTANT
    │ Observe data │              │ Clean up views   │
    └──────┬───────┘              └──────┬───────────┘
           │                             │
           ▼                             ▼
    ┌───────────────────┐         ┌─────────────┐
    │onViewStateRestored│         │ onDestroy() │
    │ State restored    │         │ Instance    │
    └──────┬────────────┘         │ destroyed   │
           │                      └──────┬──────┘
           ▼                             │
    ┌─────────────┐                      ▼
    │  onStart()  │               ┌─────────────┐
    │  Visible    │               │  onDetach() │
    └──────┬──────┘               │  Detached   │
           │                      └─────────────┘
           ▼
    ┌─────────────┐
    │  onResume() │
    │ Interactive │
    └─────────────┘
```

### ViewBinding Lifecycle

**Critical Pattern:** Always clear ViewBinding reference in `onDestroyView()` to prevent memory leaks:

```kotlin
abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    
    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!
    
    override fun onCreateView(...): View {
        _binding = createBinding(inflater, container)
        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // ← CRITICAL: Prevent memory leaks
    }
}
```

### ViewModel Lifecycle

```kotlin
@HiltViewModel
class TimerListViewModel @Inject constructor(...) : ViewModel() {
    
    // ════════════════════════════════════════════════════════════
    // VIEWMODEL SCOPE
    // ════════════════════════════════════════════════════════════
    
    // All coroutines launched here are automatically cancelled
    // when ViewModel is cleared
    private val scope = viewModelScope
    
    init {
        // Called once when ViewModel is created
        // ViewModel survives configuration changes!
        loadData()
    }
    
    // ════════════════════════════════════════════════════════════
    // STATE MANAGEMENT
    // ════════════════════════════════════════════════════════════
    
    // StateFlow survives configuration changes
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    // SharedFlow for one-time events
    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()
    
    // ════════════════════════════════════════════════════════════
    // CLEANUP
    // ════════════════════════════════════════════════════════════
    
    override fun onCleared() {
        super.onCleared()
        // Called when ViewModel is no longer used
        // viewModelScope coroutines are auto-cancelled
        // But you can do additional cleanup here
    }
}
```

### Lifecycle Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        USER ROTATES DEVICE                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ACTIVITY                    VIEWMODEL                          │
│  ────────                    ─────────                          │
│                                                                  │
│  ┌──────────────┐           ┌──────────────────┐                │
│  │ Old Activity │           │                  │                │
│  │ onPause()    │           │                  │                │
│  │ onStop()     │           │    ViewModel     │                │
│  │ onDestroy()  │───────────│    SURVIVES!     │                │
│  └──────────────┘           │                  │                │
│         │                   │  StateFlow data  │                │
│         │                   │  is preserved    │                │
│         ▼                   │                  │                │
│  ┌──────────────┐           │                  │                │
│  │ New Activity │           │                  │                │
│  │ onCreate()   │───────────│                  │                │
│  │ onStart()    │           │                  │                │
│  │ onResume()   │           └──────────────────┘                │
│  └──────────────┘                                               │
│                                                                  │
│  Result: UI state is preserved across rotation!                  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Navigation Component

### Navigation Graph Structure

```xml
<!-- cooking_timer_nav_graph.xml -->
<navigation
    app:startDestination="@id/timerListFragment">
    
    ┌─────────────────────────────────────────────────┐
    │              timerListFragment                   │
    │              (Start Destination)                 │
    │                                                  │
    │  ┌──────────────────────────────────────────┐   │
    │  │ action_timerList_to_timerDetail          │   │
    │  │ action_timerList_to_createTimer          │   │
    │  │ action_timerList_to_presets              │   │
    │  └──────────────────────────────────────────┘   │
    └────────────────────┬────────────────────────────┘
                         │
         ┌───────────────┼───────────────┐
         │               │               │
         ▼               ▼               ▼
    ┌─────────┐    ┌──────────┐    ┌─────────┐
    │ Detail  │    │  Create  │    │ Presets │
    │Fragment │    │ Fragment │    │Fragment │
    │         │    │          │    │         │
    │ Args:   │    │ Args:    │    │         │
    │ timerId │    │ presetId │    │         │
    │(required│    │(optional)│    │         │
    └─────────┘    └──────────┘    └─────────┘
```

### Navigation Actions

```kotlin
// Navigate with Safe Args (type-safe)
val action = TimerListFragmentDirections
    .actionTimerListToTimerDetail(timerId = "123")
findNavController().navigate(action)

// Navigate with action ID
findNavController().navigate(R.id.action_timerList_to_createTimer)

// Navigate back
findNavController().navigateUp()
```

### Transition Animations

```xml
<action
    android:id="@+id/action_timerList_to_timerDetail"
    app:destination="@id/timerDetailFragment"
    app:enterAnim="@anim/slide_in_right"      <!-- New fragment enters -->
    app:exitAnim="@anim/slide_out_left"       <!-- Old fragment exits -->
    app:popEnterAnim="@anim/slide_in_left"    <!-- Returns: old enters -->
    app:popExitAnim="@anim/slide_out_right"/> <!-- Returns: new exits -->
```

---

## ViewBinding

### Setup in build.gradle.kts

```kotlin
android {
    buildFeatures {
        viewBinding = true
    }
}
```

### Activity Usage

```kotlin
class CookingTimerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCookingTimerBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inflate using ViewBinding
        binding = ActivityCookingTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Access views type-safely
        binding.toolbar.title = "Cooking Timer"
    }
}
```

### Fragment Usage with BaseFragment

```kotlin
class TimerListFragment : BaseFragment<FragmentTimerListBinding>() {
    
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTimerListBinding {
        return FragmentTimerListBinding.inflate(inflater, container, false)
    }
    
    override fun setupViews() {
        // Access views through 'binding' property
        binding.recyclerViewTimers.adapter = adapter
        binding.fabCreateTimer.setOnClickListener { ... }
    }
}
```

---

## ViewModel & State Management

### UI State Pattern

```kotlin
// Immutable data class representing UI state
data class TimerListUiState(
    val timers: List<CookingTimer> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val activeTimersCount: Int = 0
) {
    // Derived properties
    val isEmpty: Boolean get() = !isLoading && timers.isEmpty()
    val hasContent: Boolean get() = timers.isNotEmpty()
}
```

### One-Time Events Pattern

```kotlin
// Sealed class for events consumed once by UI
sealed class TimerListEvent {
    data class NavigateToDetail(val timerId: String) : TimerListEvent()
    data object NavigateToCreate : TimerListEvent()
    data class ShowMessage(val message: String) : TimerListEvent()
}
```

### Observing State in Fragment

```kotlin
override fun observeState() {
    // Observe StateFlow with lifecycle awareness
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect { state ->
                updateUiState(state)
            }
        }
    }
    
    // Observe one-time events
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect { event ->
                handleEvent(event)
            }
        }
    }
}
```

---

## Dependency Injection

### Hilt Setup

```kotlin
// Activity
@AndroidEntryPoint
class CookingTimerActivity : AppCompatActivity()

// Fragment
@AndroidEntryPoint
class TimerListFragment : BaseFragment<FragmentTimerListBinding>()

// ViewModel
@HiltViewModel
class TimerListViewModel @Inject constructor(
    private val getTimersUseCase: GetTimersUseCase,
    private val startTimerUseCase: StartTimerUseCase
) : ViewModel()
```

### DI Module

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class CookingTimerModule {
    
    // Bind interface to implementation
    @Binds
    @Singleton
    abstract fun bindTimerRepository(
        impl: TimerRepositoryImpl
    ): TimerRepository
}
```

---

## Testing

### Test Structure

| Test File | Purpose |
|-----------|---------|
| `FakeTimerRepository.kt` | Fake repository for testing |
| `CookingTimerTest.kt` | Domain model tests |
| `CreateTimerUseCaseTest.kt` | Create use case tests |
| `GetTimersUseCaseTest.kt` | Query use case tests |
| `TimerControlUseCaseTest.kt` | Control actions tests |
| `TimerListViewModelTest.kt` | List ViewModel tests |
| `CreateTimerViewModelTest.kt` | Create ViewModel tests |

### Running Tests

```bash
# Run all tests for this module
./gradlew :features:cooking-timer:test

# Run specific test class
./gradlew :features:cooking-timer:testDebugUnitTest --tests "*TimerListViewModelTest"
```

### Example Test

```kotlin
@Test
fun `onStartTimer changes timer status`() = runTest {
    // Given
    val timer = createTimer("1", TimerStatus.IDLE)
    repository.addTimer(timer)
    
    // When
    viewModel.onStartTimer("1")
    testDispatcher.scheduler.advanceUntilIdle()
    
    // Then
    val updatedTimer = repository.getTimerById("1")
    assertThat(updatedTimer.status).isEqualTo(TimerStatus.RUNNING)
}
```

---

## How to Use

### Launching the Feature

The Cooking Timer feature is integrated into the app in multiple places:

#### 1. From Home Screen
- **Timer Icon in Toolbar** - Tap the timer icon in the top app bar
- **Floating Action Button** - Tap the timer FAB at the bottom right

#### 2. From Recipe Detail Screen  
- **Timer Icon in Toolbar** - Starts timer with recipe's cook time
- **"Start Timer" FAB** - Extended FAB to start cooking timer
- **Step Timer Buttons** - Individual "Set Timer" buttons on cooking steps

### Programmatic Launch

```kotlin
// From anywhere in your app
import com.eslam.bakingapp.features.cookingtimer.presentation.activity.CookingTimerActivity

// Simple launch
startActivity(CookingTimerActivity.createIntent(context))

// Launch with specific timer ID
startActivity(CookingTimerActivity.createIntent(context, timerId = "123"))
```

### From Compose Navigation

```kotlin
// In BakingNavHost.kt
val context = LocalContext.current

// Launch timer activity
context.startActivity(CookingTimerActivity.createIntent(context))
```

### Deep Linking (if needed)

Add to `AndroidManifest.xml`:

```xml
<activity android:name=".presentation.activity.CookingTimerActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <data
            android:scheme="bakingapp"
            android:host="timer" />
    </intent-filter>
</activity>
```

---

## Resources

### Dependencies Used

| Dependency | Purpose |
|------------|---------|
| `androidx.navigation:navigation-fragment-ktx` | Navigation Component |
| `androidx.navigation:navigation-ui-ktx` | ActionBar integration |
| `androidx.fragment:fragment-ktx` | Fragment extensions |
| `androidx.appcompat:appcompat` | AppCompat support |
| `com.google.android.material:material` | Material Design |
| `androidx.constraintlayout:constraintlayout` | ConstraintLayout |
| `com.google.dagger:hilt-android` | Dependency Injection |
| `androidx.lifecycle:lifecycle-viewmodel-ktx` | ViewModel |
| `org.jetbrains.kotlinx:kotlinx-coroutines-android` | Coroutines |

### Key Files to Study

1. **Lifecycle:** `CookingTimerActivity.kt`, `BaseFragment.kt`
2. **Navigation:** `cooking_timer_nav_graph.xml`
3. **State Management:** `TimerListViewModel.kt`, `TimerListUiState.kt`
4. **ViewBinding:** `BaseFragment.kt`, any Fragment
5. **Testing:** `TimerListViewModelTest.kt`

### Logcat Tags for Debugging

```
CookingTimerActivity  - Activity lifecycle events
BaseFragment          - Fragment lifecycle events
TimerListViewModel    - ViewModel events and state changes
TimerDetailViewModel  - Timer detail events
CreateTimerViewModel  - Timer creation events
```

---

## Summary

This feature module demonstrates:

✅ **Activity with XML layout** - Traditional View-based UI  
✅ **Fragment Navigation** - Navigation Component with multiple fragments  
✅ **Lifecycle Management** - Proper handling of Activity, Fragment, ViewModel lifecycles  
✅ **ViewBinding** - Type-safe view access with proper cleanup  
✅ **Clean Architecture** - Domain, Data, Presentation layers  
✅ **MVVM Pattern** - ViewModel with StateFlow and events  
✅ **Dependency Injection** - Hilt for DI  
✅ **Material Design 3** - Modern Material components  
✅ **Unit Testing** - Comprehensive test coverage  

---

**Author:** Android Development Team  
**Last Updated:** December 2024  
**Compatibility:** Android SDK 24+ (Android 7.0 Nougat)


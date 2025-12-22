package com.eslam.bakingapp.features.cookingtimer.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.eslam.bakingapp.features.cookingtimer.R
import com.eslam.bakingapp.features.cookingtimer.databinding.ActivityCookingTimerBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * CookingTimerActivity demonstrates Activity lifecycle management
 * with XML layouts and Navigation Component.
 * 
 * ## Activity Lifecycle Methods Demonstrated:
 * - onCreate: Initialize UI, setup navigation
 * - onStart: Activity becomes visible
 * - onResume: Activity is in foreground and interactive
 * - onPause: Activity is partially visible (dialog, another activity)
 * - onStop: Activity is no longer visible
 * - onDestroy: Activity is being destroyed
 * - onSaveInstanceState: Save state before configuration change
 * - onRestoreInstanceState: Restore state after configuration change
 * 
 * ## Key Concepts:
 * 1. ViewBinding for type-safe view access
 * 2. Navigation Component for fragment management
 * 3. AppBarConfiguration for up navigation
 * 4. Hilt for dependency injection
 * 5. Lifecycle observers for component awareness
 */
@AndroidEntryPoint
class CookingTimerActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "CookingTimerActivity"
        private const val KEY_NAVIGATION_STATE = "navigation_state"
        
        /**
         * Creates an Intent to launch this activity.
         * Demonstrates the recommended pattern for starting activities.
         */
        fun createIntent(context: Context): Intent {
            return Intent(context, CookingTimerActivity::class.java)
        }
        
        /**
         * Creates an Intent with a specific timer ID to view.
         */
        fun createIntent(context: Context, timerId: String): Intent {
            return Intent(context, CookingTimerActivity::class.java).apply {
                putExtra("timer_id", timerId)
            }
        }
    }
    
    private lateinit var binding: ActivityCookingTimerBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    
    /**
     * Custom lifecycle observer to demonstrate lifecycle awareness.
     */
    private val lifecycleObserver = LifecycleEventObserver { source, event ->
        Log.d(TAG, "Lifecycle event: ${event.name}")
        when (event) {
            Lifecycle.Event.ON_CREATE -> onLifecycleCreate()
            Lifecycle.Event.ON_START -> onLifecycleStart()
            Lifecycle.Event.ON_RESUME -> onLifecycleResume()
            Lifecycle.Event.ON_PAUSE -> onLifecyclePause()
            Lifecycle.Event.ON_STOP -> onLifecycleStop()
            Lifecycle.Event.ON_DESTROY -> onLifecycleDestroy()
            else -> {}
        }
    }
    
    // ===========================================
    // LIFECYCLE: onCreate
    // ===========================================
    
    /**
     * Called when the activity is first created.
     * 
     * This is where you should:
     * - Initialize the UI (setContentView, ViewBinding)
     * - Setup navigation components
     * - Initialize ViewModels
     * - Restore saved instance state
     * - Register lifecycle observers
     * 
     * @param savedInstanceState Bundle containing saved state, null if fresh start
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate - savedInstanceState: ${savedInstanceState != null}")
        
        // Register lifecycle observer
        lifecycle.addObserver(lifecycleObserver)
        
        // Initialize ViewBinding
        binding = ActivityCookingTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        
        // Setup Navigation Component
        setupNavigation()
        
        // Handle intent extras (e.g., deep linking to specific timer)
        handleIntentExtras()
    }
    
    /**
     * Setup Navigation Component with NavHostFragment.
     * 
     * Navigation Component manages:
     * - Fragment transactions
     * - Back stack
     * - Argument passing between fragments
     * - Deep linking
     */
    private fun setupNavigation() {
        // Get NavHostFragment from layout
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        
        navController = navHostFragment.navController
        
        // Configure top-level destinations (no up button shown)
        appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(R.id.timerListFragment)
        )
        
        // Connect ActionBar with NavController
        setupActionBarWithNavController(navController, appBarConfiguration)
        
        // Listen to navigation changes
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            Log.d(TAG, "Navigation: ${destination.label}, args: $arguments")
            onDestinationChanged(destination.id)
        }
    }
    
    /**
     * Handle destination changes to update UI accordingly.
     */
    private fun onDestinationChanged(destinationId: Int) {
        when (destinationId) {
            R.id.timerListFragment -> {
                supportActionBar?.title = getString(R.string.cooking_timer_title)
            }
            R.id.timerDetailFragment -> {
                supportActionBar?.title = getString(R.string.timer_detail_title)
            }
            R.id.createTimerFragment -> {
                supportActionBar?.title = getString(R.string.create_timer_title)
            }
            R.id.timerPresetsFragment -> {
                supportActionBar?.title = getString(R.string.timer_presets_title)
            }
        }
    }
    
    /**
     * Handle extras passed via Intent.
     */
    private fun handleIntentExtras() {
        intent.getStringExtra("timer_id")?.let { timerId ->
            Log.d(TAG, "Received timer ID: $timerId")
            // Navigate to timer detail after the graph is ready
            navController.navigate(
                R.id.timerDetailFragment,
                Bundle().apply { putString("timerId", timerId) }
            )
        }
    }
    
    // ===========================================
    // LIFECYCLE: onStart
    // ===========================================
    
    /**
     * Called when the activity becomes visible to the user.
     * 
     * This is a good place to:
     * - Start animations
     * - Register broadcast receivers for UI updates
     * - Refresh data that might have changed
     */
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }
    
    // ===========================================
    // LIFECYCLE: onResume
    // ===========================================
    
    /**
     * Called when the activity is in the foreground and interactive.
     * 
     * This is a good place to:
     * - Start timer updates
     * - Resume video/audio playback
     * - Register sensors
     * - Open exclusive resources (camera)
     */
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }
    
    // ===========================================
    // LIFECYCLE: onPause
    // ===========================================
    
    /**
     * Called when the activity loses focus but is still partially visible.
     * 
     * This is a good place to:
     * - Pause animations
     * - Pause video/audio
     * - Commit unsaved changes
     * - Release exclusive resources
     * 
     * Keep this method lightweight - the next activity won't start
     * until this method completes.
     */
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }
    
    // ===========================================
    // LIFECYCLE: onStop
    // ===========================================
    
    /**
     * Called when the activity is no longer visible.
     * 
     * This is a good place to:
     * - Unregister broadcast receivers
     * - Release resources not needed while invisible
     * - Save persistent state
     */
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }
    
    // ===========================================
    // LIFECYCLE: onDestroy
    // ===========================================
    
    /**
     * Called before the activity is destroyed.
     * 
     * This is a good place to:
     * - Release all remaining resources
     * - Unregister all observers
     * - Clean up background tasks
     * 
     * Note: This might not be called in all scenarios (process killed)
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy - isFinishing: $isFinishing")
        
        // Remove lifecycle observer
        lifecycle.removeObserver(lifecycleObserver)
    }
    
    // ===========================================
    // STATE MANAGEMENT
    // ===========================================
    
    /**
     * Called to save instance state before configuration change or process death.
     * 
     * Save UI state that needs to survive:
     * - Configuration changes (rotation)
     * - Process death (low memory)
     * 
     * Navigation Component automatically saves its state.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState")
        
        // Navigation state is automatically saved by Navigation Component
        // Add any custom state here
    }
    
    /**
     * Called to restore instance state after configuration change.
     * 
     * Note: This is called after onStart()
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "onRestoreInstanceState")
    }
    
    // ===========================================
    // NAVIGATION
    // ===========================================
    
    /**
     * Handle Up navigation from ActionBar.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    
    /**
     * Handle back button press.
     * Navigation Component handles back stack automatically.
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!navController.navigateUp()) {
            super.onBackPressed()
        }
    }
    
    // ===========================================
    // LIFECYCLE OBSERVER CALLBACKS
    // ===========================================
    
    private fun onLifecycleCreate() {
        Log.d(TAG, "LifecycleObserver: ON_CREATE")
    }
    
    private fun onLifecycleStart() {
        Log.d(TAG, "LifecycleObserver: ON_START")
    }
    
    private fun onLifecycleResume() {
        Log.d(TAG, "LifecycleObserver: ON_RESUME")
    }
    
    private fun onLifecyclePause() {
        Log.d(TAG, "LifecycleObserver: ON_PAUSE")
    }
    
    private fun onLifecycleStop() {
        Log.d(TAG, "LifecycleObserver: ON_STOP")
    }
    
    private fun onLifecycleDestroy() {
        Log.d(TAG, "LifecycleObserver: ON_DESTROY")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "LifecycleObserver: ON_RESTART")
    }
}


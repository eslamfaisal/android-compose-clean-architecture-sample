package com.eslam.bakingapp.features.cookingtimer.presentation.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

/**
 * Base Fragment demonstrating Fragment lifecycle management.
 * 
 * ## Fragment Lifecycle Methods (in order):
 * 
 * ### Creation Phase:
 * 1. onAttach(Context) - Fragment attached to Activity
 * 2. onCreate(Bundle?) - Fragment instance created
 * 3. onCreateView(...) - Create view hierarchy
 * 4. onViewCreated(View, Bundle?) - View hierarchy created
 * 5. onViewStateRestored(Bundle?) - View state restored
 * 6. onStart() - Fragment becomes visible
 * 7. onResume() - Fragment is interactive
 * 
 * ### Destruction Phase:
 * 8. onPause() - Fragment losing focus
 * 9. onStop() - Fragment no longer visible
 * 10. onSaveInstanceState(Bundle) - Save state
 * 11. onDestroyView() - View hierarchy destroyed
 * 12. onDestroy() - Fragment instance destroyed
 * 13. onDetach() - Fragment detached from Activity
 * 
 * ## Key Concepts:
 * - View lifecycle is separate from Fragment lifecycle
 * - ViewBinding should be cleared in onDestroyView
 * - Use viewLifecycleOwner for view-related observers
 * 
 * @param VB The ViewBinding type for this fragment
 */
abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    
    companion object {
        private const val TAG = "BaseFragment"
    }
    
    /**
     * ViewBinding reference.
     * Only valid between onCreateView and onDestroyView.
     */
    private var _binding: VB? = null
    
    /**
     * Safe access to binding - throws if accessed outside valid lifecycle.
     */
    protected val binding: VB
        get() = _binding ?: throw IllegalStateException(
            "ViewBinding accessed outside of valid lifecycle (onCreateView to onDestroyView)"
        )
    
    /**
     * Lifecycle observer for logging and debugging.
     */
    private val lifecycleObserver = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            Log.d(TAG, "${this@BaseFragment::class.simpleName} - Fragment Lifecycle: ${event.name}")
        }
    }
    
    /**
     * View lifecycle observer.
     */
    private val viewLifecycleObserver = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            Log.d(TAG, "${this@BaseFragment::class.simpleName} - View Lifecycle: ${event.name}")
        }
    }
    
    /**
     * Abstract method for inflating ViewBinding.
     */
    abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    
    // ===========================================
    // FRAGMENT LIFECYCLE: Creation Phase
    // ===========================================
    
    /**
     * Called when fragment is first attached to its context.
     * 
     * Use this to:
     * - Get reference to activity
     * - Initialize callbacks that require activity
     * - Note: View is NOT available yet
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "${this::class.simpleName} - onAttach")
        
        // Register fragment lifecycle observer
        lifecycle.addObserver(lifecycleObserver)
    }
    
    /**
     * Called when fragment is first created.
     * 
     * Use this to:
     * - Initialize non-UI data
     * - Retain fragment across config changes
     * - Note: View is NOT available yet
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "${this::class.simpleName} - onCreate - savedInstanceState: ${savedInstanceState != null}")
    }
    
    /**
     * Called to create the view hierarchy.
     * 
     * Use this to:
     * - Inflate layout using ViewBinding
     * - Return the root view
     * - Note: View is being created but not fully initialized
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "${this::class.simpleName} - onCreateView")
        _binding = createBinding(inflater, container)
        return binding.root
    }
    
    /**
     * Called immediately after onCreateView.
     * 
     * Use this to:
     * - Setup views (listeners, adapters)
     * - Initialize view-related components
     * - Observe LiveData/StateFlow
     * - The view is fully created and ready
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "${this::class.simpleName} - onViewCreated")
        
        // Register view lifecycle observer
        viewLifecycleOwner.lifecycle.addObserver(viewLifecycleObserver)
        
        // Call setup methods
        setupViews()
        observeState()
    }
    
    /**
     * Template method for setting up views.
     * Override in subclasses.
     */
    protected open fun setupViews() {
        // Override in subclasses
    }
    
    /**
     * Template method for observing state.
     * Override in subclasses.
     */
    protected open fun observeState() {
        // Override in subclasses
    }
    
    /**
     * Called after onViewCreated when view state is restored.
     * 
     * Use this to:
     * - Read restored view state
     * - Initialize state-dependent UI
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.d(TAG, "${this::class.simpleName} - onViewStateRestored")
    }
    
    /**
     * Called when fragment becomes visible.
     * 
     * Use this to:
     * - Start animations
     * - Register visible-only observers
     */
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "${this::class.simpleName} - onStart")
    }
    
    /**
     * Called when fragment is in foreground and interactive.
     * 
     * Use this to:
     * - Resume active operations
     * - Start input handling
     */
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "${this::class.simpleName} - onResume")
    }
    
    // ===========================================
    // FRAGMENT LIFECYCLE: Destruction Phase
    // ===========================================
    
    /**
     * Called when fragment loses focus.
     * 
     * Use this to:
     * - Pause active operations
     * - Commit unsaved changes
     */
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "${this::class.simpleName} - onPause")
    }
    
    /**
     * Called when fragment is no longer visible.
     * 
     * Use this to:
     * - Stop animations
     * - Unregister visible-only observers
     */
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "${this::class.simpleName} - onStop")
    }
    
    /**
     * Called to save instance state.
     * 
     * Use this to:
     * - Save UI state
     * - Save user input that would be lost
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "${this::class.simpleName} - onSaveInstanceState")
    }
    
    /**
     * Called when view hierarchy is destroyed.
     * 
     * IMPORTANT: 
     * - Clear ViewBinding reference to prevent memory leaks
     * - View-related resources should be released here
     * - Fragment itself might still exist (on back stack)
     */
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "${this::class.simpleName} - onDestroyView")
        
        // CRITICAL: Clear binding to prevent memory leaks
        _binding = null
    }
    
    /**
     * Called when fragment instance is destroyed.
     * 
     * Use this to:
     * - Release fragment-level resources
     * - Cancel background tasks
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "${this::class.simpleName} - onDestroy")
    }
    
    /**
     * Called when fragment is detached from activity.
     * 
     * Use this to:
     * - Clear activity references
     * - Final cleanup
     */
    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "${this::class.simpleName} - onDetach")
        
        // Remove lifecycle observer
        lifecycle.removeObserver(lifecycleObserver)
    }
}


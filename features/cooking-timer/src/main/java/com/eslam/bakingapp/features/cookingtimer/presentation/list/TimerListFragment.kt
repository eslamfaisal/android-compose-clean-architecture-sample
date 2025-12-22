package com.eslam.bakingapp.features.cookingtimer.presentation.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslam.bakingapp.features.cookingtimer.R
import com.eslam.bakingapp.features.cookingtimer.databinding.FragmentTimerListBinding
import com.eslam.bakingapp.features.cookingtimer.presentation.base.BaseFragment
import com.eslam.bakingapp.features.cookingtimer.presentation.list.adapter.TimerListAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment displaying the list of cooking timers.
 * 
 * Demonstrates:
 * - Fragment lifecycle with ViewBinding
 * - ViewModel observation with lifecycle awareness
 * - Navigation Component for fragment navigation
 * - RecyclerView with proper lifecycle handling
 * - Menu handling with MenuProvider
 */
@AndroidEntryPoint
class TimerListFragment : BaseFragment<FragmentTimerListBinding>() {
    
    /**
     * ViewModel scoped to this fragment.
     * Survives configuration changes.
     */
    private val viewModel: TimerListViewModel by viewModels()
    
    /**
     * RecyclerView adapter - created fresh for each view lifecycle.
     */
    private var timerAdapter: TimerListAdapter? = null
    
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTimerListBinding {
        return FragmentTimerListBinding.inflate(inflater, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup menu using MenuProvider (lifecycle-aware)
        setupMenu()
    }
    
    override fun setupViews() {
        setupRecyclerView()
        setupClickListeners()
    }
    
    /**
     * Setup RecyclerView with adapter.
     * Note: Adapter is recreated for each view lifecycle.
     */
    private fun setupRecyclerView() {
        timerAdapter = TimerListAdapter(
            onTimerClick = { timer -> viewModel.onTimerClick(timer) },
            onStartClick = { timer -> viewModel.onStartTimer(timer.id) },
            onPauseClick = { timer -> viewModel.onPauseTimer(timer.id) },
            onResetClick = { timer -> viewModel.onResetTimer(timer.id) },
            onDeleteClick = { timer -> viewModel.onDeleteTimer(timer.id) }
        )
        
        binding.recyclerViewTimers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = timerAdapter
            setHasFixedSize(true)
        }
    }
    
    /**
     * Setup click listeners for UI elements.
     */
    private fun setupClickListeners() {
        binding.fabCreateTimer.setOnClickListener {
            viewModel.onCreateTimerClick()
        }
        
        binding.buttonRetry.setOnClickListener {
            viewModel.onErrorDismissed()
        }
        
        binding.buttonAddFirstTimer.setOnClickListener {
            viewModel.onCreateTimerClick()
        }
    }
    
    /**
     * Setup menu using lifecycle-aware MenuProvider.
     * This replaces the deprecated setHasOptionsMenu/onCreateOptionsMenu.
     */
    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        
        // Add menu provider with lifecycle awareness
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_timer_list, menu)
            }
            
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_presets -> {
                        viewModel.onPresetsClick()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    
    override fun observeState() {
        // Observe UI state with lifecycle awareness
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
    
    /**
     * Update UI based on state.
     */
    private fun updateUiState(state: TimerListUiState) {
        binding.apply {
            // Loading state
            progressBar.isVisible = state.isLoading
            
            // Error state
            layoutError.isVisible = state.errorMessage != null
            textViewError.text = state.errorMessage
            
            // Empty state
            layoutEmpty.isVisible = state.isEmpty
            
            // Content state
            recyclerViewTimers.isVisible = state.hasContent
            
            // Update adapter
            if (state.hasContent) {
                timerAdapter?.submitList(state.timers)
            }
            
            // Active timers badge
            textViewActiveCount.apply {
                isVisible = state.activeTimersCount > 0
                text = getString(R.string.active_timers_count, state.activeTimersCount)
            }
        }
    }
    
    /**
     * Handle one-time events.
     */
    private fun handleEvent(event: TimerListEvent) {
        when (event) {
            is TimerListEvent.NavigateToDetail -> {
                // Navigate with Bundle arguments (without Safe Args)
                val bundle = Bundle().apply {
                    putString("timerId", event.timerId)
                }
                findNavController().navigate(R.id.action_timerList_to_timerDetail, bundle)
            }
            is TimerListEvent.NavigateToCreate -> {
                findNavController().navigate(R.id.action_timerList_to_createTimer)
            }
            is TimerListEvent.NavigateToPresets -> {
                findNavController().navigate(R.id.action_timerList_to_presets)
            }
            is TimerListEvent.ShowMessage -> {
                Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is TimerListEvent.TimerCompleted -> {
                // Could trigger notification here
                Snackbar.make(
                    binding.root,
                    getString(R.string.timer_completed_message, event.timer.name),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }
    
    override fun onDestroyView() {
        // Clean up adapter reference before view is destroyed
        binding.recyclerViewTimers.adapter = null
        timerAdapter = null
        super.onDestroyView()
    }
}


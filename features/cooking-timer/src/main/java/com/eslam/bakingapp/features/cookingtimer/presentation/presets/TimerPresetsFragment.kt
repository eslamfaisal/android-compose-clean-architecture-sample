package com.eslam.bakingapp.features.cookingtimer.presentation.presets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslam.bakingapp.features.cookingtimer.databinding.FragmentTimerPresetsBinding
import com.eslam.bakingapp.features.cookingtimer.domain.model.PresetCategory
import com.eslam.bakingapp.features.cookingtimer.presentation.base.BaseFragment
import com.eslam.bakingapp.features.cookingtimer.presentation.presets.adapter.PresetsAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment displaying timer presets organized by category.
 * 
 * Demonstrates:
 * - ChipGroup for category filtering
 * - Grouped RecyclerView items
 * - Quick timer creation from presets
 */
@AndroidEntryPoint
class TimerPresetsFragment : BaseFragment<FragmentTimerPresetsBinding>() {
    
    private val viewModel: TimerPresetsViewModel by viewModels()
    private var presetsAdapter: PresetsAdapter? = null
    
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTimerPresetsBinding {
        return FragmentTimerPresetsBinding.inflate(inflater, container, false)
    }
    
    override fun setupViews() {
        setupCategoryChips()
        setupRecyclerView()
    }
    
    private fun setupCategoryChips() {
        binding.chipGroupCategories.apply {
            // Add "All" chip
            val allChip = Chip(requireContext()).apply {
                text = "All"
                isCheckable = true
                isChecked = true
            }
            addView(allChip)
            
            // Add category chips
            PresetCategory.entries.forEach { category ->
                val chip = Chip(requireContext()).apply {
                    text = category.toDisplayString()
                    isCheckable = true
                    tag = category
                }
                addView(chip)
            }
            
            setOnCheckedStateChangeListener { _, checkedIds ->
                if (checkedIds.isEmpty()) {
                    // If nothing selected, select "All"
                    allChip.isChecked = true
                    return@setOnCheckedStateChangeListener
                }
                
                val selectedChip = findViewById<Chip>(checkedIds.first())
                val category = selectedChip?.tag as? PresetCategory
                viewModel.onCategorySelected(category)
            }
        }
    }
    
    private fun setupRecyclerView() {
        presetsAdapter = PresetsAdapter { preset ->
            viewModel.onPresetClick(preset)
        }
        
        binding.recyclerViewPresets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = presetsAdapter
        }
    }
    
    override fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUiState(state)
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    handleEvent(event)
                }
            }
        }
    }
    
    private fun updateUiState(state: TimerPresetsUiState) {
        binding.apply {
            progressBar.isVisible = state.isLoading
            recyclerViewPresets.isVisible = !state.isLoading && state.errorMessage == null
            layoutError.isVisible = state.errorMessage != null
            textViewError.text = state.errorMessage
            
            presetsAdapter?.submitList(state.filteredPresets)
        }
    }
    
    private fun handleEvent(event: TimerPresetsEvent) {
        when (event) {
            is TimerPresetsEvent.NavigateBack -> {
                findNavController().navigateUp()
            }
            is TimerPresetsEvent.ShowMessage -> {
                Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is TimerPresetsEvent.TimerCreatedFromPreset -> {
                // Timer created, navigating back
            }
        }
    }
    
    override fun onDestroyView() {
        binding.recyclerViewPresets.adapter = null
        presetsAdapter = null
        super.onDestroyView()
    }
}


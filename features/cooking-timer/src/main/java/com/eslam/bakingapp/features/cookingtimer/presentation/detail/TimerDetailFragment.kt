package com.eslam.bakingapp.features.cookingtimer.presentation.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.eslam.bakingapp.features.cookingtimer.R
import com.eslam.bakingapp.features.cookingtimer.databinding.FragmentTimerDetailBinding
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerStatus
import com.eslam.bakingapp.features.cookingtimer.presentation.base.BaseFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment displaying timer details with large countdown display.
 * 
 * Demonstrates:
 * - Navigation arguments via Safe Args
 * - Fragment-scoped ViewModel
 * - Material Design dialogs
 */
@AndroidEntryPoint
class TimerDetailFragment : BaseFragment<FragmentTimerDetailBinding>() {
    
    private val viewModel: TimerDetailViewModel by viewModels()
    
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTimerDetailBinding {
        return FragmentTimerDetailBinding.inflate(inflater, container, false)
    }
    
    override fun setupViews() {
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.apply {
            buttonStart.setOnClickListener { viewModel.onStartTimer() }
            buttonPause.setOnClickListener { viewModel.onPauseTimer() }
            buttonReset.setOnClickListener { viewModel.onResetTimer() }
            buttonDelete.setOnClickListener { showDeleteConfirmation() }
        }
    }
    
    private fun showDeleteConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_timer_title)
            .setMessage(R.string.delete_timer_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.onDeleteTimer()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
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
    
    private fun updateUiState(state: TimerDetailUiState) {
        binding.apply {
            progressBar.isVisible = state.isLoading
            layoutContent.isVisible = state.hasTimer
            layoutError.isVisible = state.errorMessage != null
            textViewError.text = state.errorMessage
            
            state.timer?.let { timer ->
                textViewTimerName.text = timer.name
                textViewDescription.text = timer.description
                textViewRemainingTime.text = timer.formattedRemainingTime
                textViewTotalDuration.text = getString(
                    R.string.total_duration_format,
                    timer.formattedDuration
                )
                
                circularProgress.progress = (timer.progress * 100).toInt()
                textViewStatus.text = timer.status.toDisplayString()
                
                // Update button states
                when (timer.status) {
                    TimerStatus.IDLE -> {
                        buttonStart.isEnabled = true
                        buttonPause.isEnabled = false
                        buttonReset.isEnabled = false
                    }
                    TimerStatus.RUNNING -> {
                        buttonStart.isEnabled = false
                        buttonPause.isEnabled = true
                        buttonReset.isEnabled = true
                    }
                    TimerStatus.PAUSED -> {
                        buttonStart.isEnabled = true
                        buttonPause.isEnabled = false
                        buttonReset.isEnabled = true
                    }
                    TimerStatus.COMPLETED, TimerStatus.CANCELLED -> {
                        buttonStart.isEnabled = false
                        buttonPause.isEnabled = false
                        buttonReset.isEnabled = true
                    }
                }
            }
        }
    }
    
    private fun handleEvent(event: TimerDetailEvent) {
        when (event) {
            is TimerDetailEvent.NavigateBack -> {
                findNavController().navigateUp()
            }
            is TimerDetailEvent.ShowMessage -> {
                Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}


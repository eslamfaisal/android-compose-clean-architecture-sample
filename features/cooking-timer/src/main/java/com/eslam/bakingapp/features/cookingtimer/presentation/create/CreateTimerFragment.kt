package com.eslam.bakingapp.features.cookingtimer.presentation.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.eslam.bakingapp.features.cookingtimer.databinding.FragmentCreateTimerBinding
import com.eslam.bakingapp.features.cookingtimer.presentation.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment for creating a new timer.
 * 
 * Demonstrates:
 * - Form handling with TextInputLayout
 * - NumberPicker for time selection
 * - Input validation with error display
 */
@AndroidEntryPoint
class CreateTimerFragment : BaseFragment<FragmentCreateTimerBinding>() {
    
    private val viewModel: CreateTimerViewModel by viewModels()
    
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateTimerBinding {
        return FragmentCreateTimerBinding.inflate(inflater, container, false)
    }
    
    override fun setupViews() {
        setupInputListeners()
        setupNumberPickers()
        setupButtonListeners()
    }
    
    private fun setupInputListeners() {
        binding.apply {
            editTextName.doAfterTextChanged { text ->
                viewModel.onNameChanged(text?.toString() ?: "")
            }
            
            editTextDescription.doAfterTextChanged { text ->
                viewModel.onDescriptionChanged(text?.toString() ?: "")
            }
        }
    }
    
    private fun setupNumberPickers() {
        binding.apply {
            numberPickerHours.apply {
                minValue = 0
                maxValue = 23
                wrapSelectorWheel = true
                setOnValueChangedListener { _, _, newVal ->
                    viewModel.onHoursChanged(newVal)
                }
            }
            
            numberPickerMinutes.apply {
                minValue = 0
                maxValue = 59
                value = 5 // Default 5 minutes
                wrapSelectorWheel = true
                setOnValueChangedListener { _, _, newVal ->
                    viewModel.onMinutesChanged(newVal)
                }
            }
            
            numberPickerSeconds.apply {
                minValue = 0
                maxValue = 59
                wrapSelectorWheel = true
                setOnValueChangedListener { _, _, newVal ->
                    viewModel.onSecondsChanged(newVal)
                }
            }
        }
    }
    
    private fun setupButtonListeners() {
        binding.apply {
            buttonCreate.setOnClickListener {
                viewModel.onCreateTimer()
            }
            
            buttonCancel.setOnClickListener {
                viewModel.onCancelClick()
            }
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
    
    private fun updateUiState(state: CreateTimerUiState) {
        binding.apply {
            // Update number pickers (only if different to avoid loops)
            if (numberPickerHours.value != state.hours) {
                numberPickerHours.value = state.hours
            }
            if (numberPickerMinutes.value != state.minutes) {
                numberPickerMinutes.value = state.minutes
            }
            if (numberPickerSeconds.value != state.seconds) {
                numberPickerSeconds.value = state.seconds
            }
            
            // Update errors
            textInputLayoutName.error = state.nameError
            textViewDurationError.apply {
                isVisible = state.durationError != null
                text = state.durationError
            }
            
            // Update duration preview
            textViewDurationPreview.text = state.formattedDuration
            
            // Update button state
            buttonCreate.isEnabled = state.isValid && !state.isCreating
            progressBar.isVisible = state.isCreating
        }
    }
    
    private fun handleEvent(event: CreateTimerEvent) {
        when (event) {
            is CreateTimerEvent.NavigateBack -> {
                findNavController().navigateUp()
            }
            is CreateTimerEvent.ShowMessage -> {
                Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
            }
            is CreateTimerEvent.TimerCreated -> {
                // Could navigate to detail here instead of back
            }
        }
    }
}


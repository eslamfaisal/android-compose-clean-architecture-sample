package com.eslam.bakingapp.features.cookingtimer.presentation.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eslam.bakingapp.features.cookingtimer.R
import com.eslam.bakingapp.features.cookingtimer.databinding.ItemTimerBinding
import com.eslam.bakingapp.features.cookingtimer.domain.model.CookingTimer
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerStatus

/**
 * RecyclerView Adapter for displaying timers.
 * 
 * Uses ListAdapter with DiffUtil for efficient updates.
 * Demonstrates proper adapter pattern with click listeners.
 */
class TimerListAdapter(
    private val onTimerClick: (CookingTimer) -> Unit,
    private val onStartClick: (CookingTimer) -> Unit,
    private val onPauseClick: (CookingTimer) -> Unit,
    private val onResetClick: (CookingTimer) -> Unit,
    private val onDeleteClick: (CookingTimer) -> Unit
) : ListAdapter<CookingTimer, TimerListAdapter.TimerViewHolder>(TimerDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val binding = ItemTimerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TimerViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class TimerViewHolder(
        private val binding: ItemTimerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(timer: CookingTimer) {
            binding.apply {
                // Timer info
                textViewTimerName.text = timer.name
                textViewTimerDescription.text = timer.description
                textViewRemainingTime.text = timer.formattedRemainingTime
                textViewTotalDuration.text = root.context.getString(
                    R.string.of_duration,
                    timer.formattedDuration
                )
                
                // Progress
                progressBarTimer.progress = (timer.progress * 100).toInt()
                
                // Status indicator
                textViewStatus.text = timer.status.toDisplayString()
                textViewStatus.setTextColor(getStatusColor(timer.status))
                
                // Button visibility based on status
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
                
                // Click listeners
                root.setOnClickListener { onTimerClick(timer) }
                buttonStart.setOnClickListener { onStartClick(timer) }
                buttonPause.setOnClickListener { onPauseClick(timer) }
                buttonReset.setOnClickListener { onResetClick(timer) }
                buttonDelete.setOnClickListener { onDeleteClick(timer) }
            }
        }
        
        private fun getStatusColor(status: TimerStatus): Int {
            val colorRes = when (status) {
                TimerStatus.IDLE -> R.color.timer_status_idle
                TimerStatus.RUNNING -> R.color.timer_status_running
                TimerStatus.PAUSED -> R.color.timer_status_paused
                TimerStatus.COMPLETED -> R.color.timer_status_completed
                TimerStatus.CANCELLED -> R.color.timer_status_cancelled
            }
            return ContextCompat.getColor(binding.root.context, colorRes)
        }
    }
    
    /**
     * DiffUtil callback for efficient list updates.
     */
    class TimerDiffCallback : DiffUtil.ItemCallback<CookingTimer>() {
        override fun areItemsTheSame(oldItem: CookingTimer, newItem: CookingTimer): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: CookingTimer, newItem: CookingTimer): Boolean {
            return oldItem == newItem
        }
    }
}


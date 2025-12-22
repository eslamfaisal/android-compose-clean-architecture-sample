package com.eslam.bakingapp.features.cookingtimer.presentation.presets.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eslam.bakingapp.features.cookingtimer.R
import com.eslam.bakingapp.features.cookingtimer.databinding.ItemPresetBinding
import com.eslam.bakingapp.features.cookingtimer.domain.model.CookingTimer
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerPreset

/**
 * Adapter for displaying timer presets.
 */
class PresetsAdapter(
    private val onPresetClick: (TimerPreset) -> Unit
) : ListAdapter<TimerPreset, PresetsAdapter.PresetViewHolder>(PresetDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetViewHolder {
        val binding = ItemPresetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PresetViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: PresetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class PresetViewHolder(
        private val binding: ItemPresetBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(preset: TimerPreset) {
            binding.apply {
                textViewPresetName.text = preset.name
                textViewCategory.text = preset.category.toDisplayString()
                textViewDuration.text = CookingTimer.formatTime(preset.durationSeconds)
                
                // Category icon
                val iconRes = when (preset.category) {
                    com.eslam.bakingapp.features.cookingtimer.domain.model.PresetCategory.BOILING -> R.drawable.ic_boiling
                    com.eslam.bakingapp.features.cookingtimer.domain.model.PresetCategory.BAKING -> R.drawable.ic_baking
                    com.eslam.bakingapp.features.cookingtimer.domain.model.PresetCategory.ROASTING -> R.drawable.ic_roasting
                    com.eslam.bakingapp.features.cookingtimer.domain.model.PresetCategory.GRILLING -> R.drawable.ic_grilling
                    com.eslam.bakingapp.features.cookingtimer.domain.model.PresetCategory.SIMMERING -> R.drawable.ic_simmering
                    com.eslam.bakingapp.features.cookingtimer.domain.model.PresetCategory.RESTING -> R.drawable.ic_resting
                }
                imageViewCategory.setImageResource(iconRes)
                
                root.setOnClickListener { onPresetClick(preset) }
            }
        }
    }
    
    class PresetDiffCallback : DiffUtil.ItemCallback<TimerPreset>() {
        override fun areItemsTheSame(oldItem: TimerPreset, newItem: TimerPreset): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: TimerPreset, newItem: TimerPreset): Boolean {
            return oldItem == newItem
        }
    }
}


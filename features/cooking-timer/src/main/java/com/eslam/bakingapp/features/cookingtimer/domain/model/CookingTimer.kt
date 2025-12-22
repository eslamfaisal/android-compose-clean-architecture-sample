package com.eslam.bakingapp.features.cookingtimer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Domain model representing a Cooking Timer.
 * 
 * This model is Parcelable to demonstrate safe argument passing
 * between fragments using Navigation Component's Safe Args pattern.
 */
@Parcelize
data class CookingTimer(
    val id: String,
    val name: String,
    val description: String,
    val durationSeconds: Long,
    val remainingSeconds: Long = durationSeconds,
    val status: TimerStatus = TimerStatus.IDLE,
    val recipeId: String? = null,
    val stepNumber: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable {
    
    /**
     * Progress as a percentage (0.0 to 1.0)
     */
    val progress: Float
        get() = if (durationSeconds > 0) {
            (durationSeconds - remainingSeconds).toFloat() / durationSeconds.toFloat()
        } else 0f
    
    /**
     * Formatted time remaining (MM:SS or HH:MM:SS)
     */
    val formattedRemainingTime: String
        get() = formatTime(remainingSeconds)
    
    /**
     * Formatted total duration
     */
    val formattedDuration: String
        get() = formatTime(durationSeconds)
    
    /**
     * Check if timer is currently running
     */
    val isRunning: Boolean
        get() = status == TimerStatus.RUNNING
    
    /**
     * Check if timer is completed
     */
    val isCompleted: Boolean
        get() = status == TimerStatus.COMPLETED
    
    companion object {
        fun formatTime(totalSeconds: Long): String {
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            
            return if (hours > 0) {
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }
        }
    }
}

/**
 * Enum representing the possible states of a timer.
 * Demonstrates proper state management in domain layer.
 */
enum class TimerStatus {
    /** Timer is created but not started */
    IDLE,
    
    /** Timer is actively counting down */
    RUNNING,
    
    /** Timer is paused */
    PAUSED,
    
    /** Timer has finished counting down */
    COMPLETED,
    
    /** Timer was cancelled before completion */
    CANCELLED;
    
    fun toDisplayString(): String = when (this) {
        IDLE -> "Ready"
        RUNNING -> "Running"
        PAUSED -> "Paused"
        COMPLETED -> "Completed"
        CANCELLED -> "Cancelled"
    }
}

/**
 * Preset timer configurations for common cooking tasks.
 */
@Parcelize
data class TimerPreset(
    val id: String,
    val name: String,
    val durationSeconds: Long,
    val category: PresetCategory
) : Parcelable

/**
 * Categories for timer presets.
 */
enum class PresetCategory {
    BOILING,
    BAKING,
    ROASTING,
    GRILLING,
    SIMMERING,
    RESTING;
    
    fun toDisplayString(): String = when (this) {
        BOILING -> "Boiling"
        BAKING -> "Baking"
        ROASTING -> "Roasting"
        GRILLING -> "Grilling"
        SIMMERING -> "Simmering"
        RESTING -> "Resting"
    }
}


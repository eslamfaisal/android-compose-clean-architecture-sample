package com.eslam.bakingapp.features.cookingtimer.data.datasource

import com.eslam.bakingapp.features.cookingtimer.domain.model.CookingTimer
import com.eslam.bakingapp.features.cookingtimer.domain.model.PresetCategory
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerPreset
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data source for cooking timers.
 * 
 * This is an in-memory implementation for demonstration purposes.
 * In a production app, this would interact with Room database
 * or other persistent storage.
 */
@Singleton
class LocalTimerDataSource @Inject constructor() {
    
    private val timersFlow = MutableStateFlow<Map<String, CookingTimer>>(emptyMap())
    
    /**
     * Get all timers as a flow for real-time updates.
     */
    fun getAllTimers(): Flow<List<CookingTimer>> {
        return timersFlow.map { it.values.toList().sortedByDescending { timer -> timer.createdAt } }
    }
    
    /**
     * Get active timers (running or paused).
     */
    fun getActiveTimers(): Flow<List<CookingTimer>> {
        return timersFlow.map { timers ->
            timers.values
                .filter { it.status == TimerStatus.RUNNING || it.status == TimerStatus.PAUSED }
                .sortedByDescending { it.createdAt }
        }
    }
    
    /**
     * Get a specific timer by ID.
     */
    suspend fun getTimerById(timerId: String): CookingTimer? {
        return timersFlow.value[timerId]
    }
    
    /**
     * Insert a new timer.
     */
    suspend fun insertTimer(timer: CookingTimer) {
        timersFlow.value = timersFlow.value.toMutableMap().apply {
            put(timer.id, timer)
        }
    }
    
    /**
     * Update an existing timer.
     */
    suspend fun updateTimer(timer: CookingTimer) {
        timersFlow.value = timersFlow.value.toMutableMap().apply {
            put(timer.id, timer)
        }
    }
    
    /**
     * Delete a timer by ID.
     */
    suspend fun deleteTimer(timerId: String) {
        timersFlow.value = timersFlow.value.toMutableMap().apply {
            remove(timerId)
        }
    }
    
    /**
     * Clear all completed timers.
     */
    suspend fun clearCompletedTimers(): Int {
        val currentTimers = timersFlow.value
        val completedIds = currentTimers.values
            .filter { it.status == TimerStatus.COMPLETED || it.status == TimerStatus.CANCELLED }
            .map { it.id }
        
        timersFlow.value = currentTimers.filterKeys { it !in completedIds }
        
        return completedIds.size
    }
    
    /**
     * Get predefined timer presets.
     */
    fun getTimerPresets(): List<TimerPreset> {
        return listOf(
            // Boiling presets
            TimerPreset("preset_1", "Soft Boiled Eggs", 6 * 60, PresetCategory.BOILING),
            TimerPreset("preset_2", "Hard Boiled Eggs", 12 * 60, PresetCategory.BOILING),
            TimerPreset("preset_3", "Pasta (al dente)", 8 * 60, PresetCategory.BOILING),
            TimerPreset("preset_4", "Rice", 18 * 60, PresetCategory.BOILING),
            
            // Baking presets
            TimerPreset("preset_5", "Chocolate Chip Cookies", 12 * 60, PresetCategory.BAKING),
            TimerPreset("preset_6", "Brownies", 25 * 60, PresetCategory.BAKING),
            TimerPreset("preset_7", "Banana Bread", 55 * 60, PresetCategory.BAKING),
            TimerPreset("preset_8", "Pizza", 15 * 60, PresetCategory.BAKING),
            
            // Roasting presets
            TimerPreset("preset_9", "Roasted Vegetables", 30 * 60, PresetCategory.ROASTING),
            TimerPreset("preset_10", "Roasted Chicken", 60 * 60, PresetCategory.ROASTING),
            
            // Grilling presets
            TimerPreset("preset_11", "Steak (Medium)", 8 * 60, PresetCategory.GRILLING),
            TimerPreset("preset_12", "Chicken Breast", 12 * 60, PresetCategory.GRILLING),
            TimerPreset("preset_13", "Burger Patty", 6 * 60, PresetCategory.GRILLING),
            
            // Simmering presets
            TimerPreset("preset_14", "Soup/Stew", 30 * 60, PresetCategory.SIMMERING),
            TimerPreset("preset_15", "Sauce Reduction", 20 * 60, PresetCategory.SIMMERING),
            
            // Resting presets
            TimerPreset("preset_16", "Steak Rest", 5 * 60, PresetCategory.RESTING),
            TimerPreset("preset_17", "Roast Rest", 15 * 60, PresetCategory.RESTING)
        )
    }
}


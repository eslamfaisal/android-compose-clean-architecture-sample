package com.eslam.bakingapp.features.cookingtimer.domain.usecase

import com.eslam.bakingapp.core.common.result.Result
import com.eslam.bakingapp.features.cookingtimer.domain.model.TimerPreset
import com.eslam.bakingapp.features.cookingtimer.domain.repository.TimerRepository
import javax.inject.Inject

/**
 * Use case for retrieving timer presets.
 * 
 * Presets are predefined timer configurations for common cooking tasks
 * like boiling eggs, baking cookies, etc.
 */
class GetTimerPresetsUseCase @Inject constructor(
    private val repository: TimerRepository
) {
    suspend operator fun invoke(): Result<List<TimerPreset>> {
        return repository.getTimerPresets()
    }
}


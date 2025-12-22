package com.eslam.bakingapp.features.cookingtimer.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for CookingTimer domain model.
 */
class CookingTimerTest {
    
    @Test
    fun `progress returns correct percentage`() {
        val timer = createTimer(durationSeconds = 100, remainingSeconds = 75)
        
        assertThat(timer.progress).isEqualTo(0.25f)
    }
    
    @Test
    fun `progress returns 0 when duration is 0`() {
        val timer = createTimer(durationSeconds = 0, remainingSeconds = 0)
        
        assertThat(timer.progress).isEqualTo(0f)
    }
    
    @Test
    fun `progress returns 1 when fully completed`() {
        val timer = createTimer(durationSeconds = 100, remainingSeconds = 0)
        
        assertThat(timer.progress).isEqualTo(1f)
    }
    
    @Test
    fun `formattedRemainingTime formats minutes and seconds correctly`() {
        val timer = createTimer(remainingSeconds = 125) // 2:05
        
        assertThat(timer.formattedRemainingTime).isEqualTo("02:05")
    }
    
    @Test
    fun `formattedRemainingTime includes hours when needed`() {
        val timer = createTimer(remainingSeconds = 3725) // 1:02:05
        
        assertThat(timer.formattedRemainingTime).isEqualTo("01:02:05")
    }
    
    @Test
    fun `isRunning returns true when status is RUNNING`() {
        val timer = createTimer(status = TimerStatus.RUNNING)
        
        assertThat(timer.isRunning).isTrue()
    }
    
    @Test
    fun `isRunning returns false when status is not RUNNING`() {
        val timer = createTimer(status = TimerStatus.PAUSED)
        
        assertThat(timer.isRunning).isFalse()
    }
    
    @Test
    fun `isCompleted returns true when status is COMPLETED`() {
        val timer = createTimer(status = TimerStatus.COMPLETED)
        
        assertThat(timer.isCompleted).isTrue()
    }
    
    @Test
    fun `totalTimeMinutes computed from duration`() {
        val timer = createTimer(durationSeconds = 300) // 5 minutes
        
        assertThat(timer.formattedDuration).isEqualTo("05:00")
    }
    
    @Test
    fun `formatTime static method works correctly`() {
        assertThat(CookingTimer.formatTime(0)).isEqualTo("00:00")
        assertThat(CookingTimer.formatTime(59)).isEqualTo("00:59")
        assertThat(CookingTimer.formatTime(60)).isEqualTo("01:00")
        assertThat(CookingTimer.formatTime(3600)).isEqualTo("01:00:00")
        assertThat(CookingTimer.formatTime(3661)).isEqualTo("01:01:01")
    }
    
    private fun createTimer(
        id: String = "1",
        name: String = "Test Timer",
        description: String = "Test Description",
        durationSeconds: Long = 300,
        remainingSeconds: Long = durationSeconds,
        status: TimerStatus = TimerStatus.IDLE
    ) = CookingTimer(
        id = id,
        name = name,
        description = description,
        durationSeconds = durationSeconds,
        remainingSeconds = remainingSeconds,
        status = status
    )
}


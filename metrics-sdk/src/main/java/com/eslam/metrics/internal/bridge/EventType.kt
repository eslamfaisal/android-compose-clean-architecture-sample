package com.eslam.metrics.internal.bridge

/**
 * EventType - Types of events that can be detected and recorded
 */
enum class EventType(val value: Int) {
    ANR(0),
    MEMORY_SPIKE(1),
    CPU_SPIKE(2),
    CRASH(3),
    HEAVY_ACTION(4),
    CUSTOM(5);

    companion object {
        fun fromValue(value: Int): EventType {
            return entries.find { it.value == value } ?: CUSTOM
        }
    }
}

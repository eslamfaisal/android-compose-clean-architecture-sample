package com.eslam.metrics.data.room

import androidx.room.TypeConverter
import java.util.Date

/**
 * Converters - Type converters for Room database
 */
internal class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

package com.eslam.metrics.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * SessionEntity - Room entity for storing session data
 */
@Entity(tableName = "sessions")
internal data class SessionEntity(
    @PrimaryKey
    @ColumnInfo(name = "session_id")
    val sessionId: String,

    @ColumnInfo(name = "user_id")
    val userId: String? = null,

    @ColumnInfo(name = "user_email")
    val userEmail: String? = null,

    @ColumnInfo(name = "device_model")
    val deviceModel: String,

    @ColumnInfo(name = "os_version")
    val osVersion: String,

    @ColumnInfo(name = "app_version")
    val appVersion: String,

    @ColumnInfo(name = "start_time")
    val startTime: Long,

    @ColumnInfo(name = "end_time")
    val endTime: Long? = null,

    @ColumnInfo(name = "duration_ms")
    val durationMs: Long? = null,

    @ColumnInfo(name = "event_count")
    val eventCount: Int = 0,

    @ColumnInfo(name = "is_crashed")
    val isCrashed: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

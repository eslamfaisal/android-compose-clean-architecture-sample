package com.eslam.metrics.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * EventEntity - Room entity for storing events
 */
@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["session_id"],
            childColumns = ["session_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("session_id")]
)
internal data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "session_id")
    val sessionId: String,

    @ColumnInfo(name = "event_type")
    val eventType: String,

    @ColumnInfo(name = "event_name")
    val eventName: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "metadata_json")
    val metadataJson: String? = null,

    @ColumnInfo(name = "screenshot_path")
    val screenshotPath: String? = null,

    @ColumnInfo(name = "memory_usage_mb")
    val memoryUsageMb: Long? = null,

    @ColumnInfo(name = "cpu_usage_percent")
    val cpuUsagePercent: Float? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

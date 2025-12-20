package com.eslam.metrics.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * EventDao - Data Access Object for event operations
 */
@Dao
internal interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity): Long

    @Query("SELECT * FROM events WHERE session_id = :sessionId ORDER BY timestamp DESC")
    suspend fun getEventsBySessionId(sessionId: String): List<EventEntity>

    @Query("SELECT * FROM events WHERE session_id = :sessionId ORDER BY timestamp DESC")
    fun observeEventsBySessionId(sessionId: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentEvents(limit: Int): List<EventEntity>

    @Query("SELECT * FROM events WHERE event_type = :eventType ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getEventsByType(eventType: String, limit: Int): List<EventEntity>

    @Query("SELECT COUNT(*) FROM events WHERE session_id = :sessionId")
    suspend fun getEventCountForSession(sessionId: String): Int

    @Query("DELETE FROM events WHERE created_at < :timestamp")
    suspend fun deleteOldEvents(timestamp: Long)

    @Query("DELETE FROM events WHERE session_id = :sessionId")
    suspend fun deleteEventsForSession(sessionId: String)
}

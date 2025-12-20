package com.eslam.metrics.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * SessionDao - Data Access Object for session operations
 */
@Dao
internal interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    @Update
    suspend fun updateSession(session: SessionEntity)

    @Query("SELECT * FROM sessions WHERE session_id = :sessionId")
    suspend fun getSessionById(sessionId: String): SessionEntity?

    @Query("SELECT * FROM sessions ORDER BY start_time DESC LIMIT :limit")
    suspend fun getRecentSessions(limit: Int): List<SessionEntity>

    @Query("SELECT * FROM sessions ORDER BY start_time DESC")
    fun observeAllSessions(): Flow<List<SessionEntity>>

    @Query("UPDATE sessions SET end_time = :endTime, duration_ms = :durationMs WHERE session_id = :sessionId")
    suspend fun endSession(sessionId: String, endTime: Long, durationMs: Long)

    @Query("UPDATE sessions SET event_count = event_count + 1 WHERE session_id = :sessionId")
    suspend fun incrementEventCount(sessionId: String)

    @Query("UPDATE sessions SET is_crashed = 1 WHERE session_id = :sessionId")
    suspend fun markAsCrashed(sessionId: String)

    @Query("DELETE FROM sessions WHERE created_at < :timestamp")
    suspend fun deleteOldSessions(timestamp: Long)

    @Query("SELECT COUNT(*) FROM sessions")
    suspend fun getSessionCount(): Int
}

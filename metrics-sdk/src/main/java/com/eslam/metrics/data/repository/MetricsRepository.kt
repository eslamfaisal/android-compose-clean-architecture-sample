package com.eslam.metrics.data.repository

import android.content.Context
import android.os.Build
import com.eslam.metrics.data.room.EventEntity
import com.eslam.metrics.data.room.MetricsDatabase
import com.eslam.metrics.data.room.SessionEntity
import com.eslam.metrics.internal.bridge.EventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * MetricsRepository - Repository pattern implementation for data operations
 */
internal class MetricsRepository(context: Context) {

    private val database = MetricsDatabase.getInstance(context)
    private val sessionDao = database.sessionDao()
    private val eventDao = database.eventDao()
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private val appVersion: String = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "unknown"
    } catch (e: Exception) {
        "unknown"
    }

    // Session operations
    
    fun createSession(
        sessionId: String,
        userId: String?,
        userEmail: String?
    ) {
        scope.launch {
            val session = SessionEntity(
                sessionId = sessionId,
                userId = userId,
                userEmail = userEmail,
                deviceModel = Build.MODEL,
                osVersion = "Android ${Build.VERSION.RELEASE}",
                appVersion = appVersion,
                startTime = System.currentTimeMillis()
            )
            sessionDao.insertSession(session)
        }
    }

    fun endSession(sessionId: String, durationMs: Long) {
        scope.launch {
            sessionDao.endSession(
                sessionId = sessionId,
                endTime = System.currentTimeMillis(),
                durationMs = durationMs
            )
        }
    }

    suspend fun getSession(sessionId: String): SessionEntity? {
        return sessionDao.getSessionById(sessionId)
    }

    suspend fun getRecentSessions(limit: Int = 10): List<SessionEntity> {
        return sessionDao.getRecentSessions(limit)
    }

    fun observeAllSessions(): Flow<List<SessionEntity>> {
        return sessionDao.observeAllSessions()
    }

    // Event operations

    fun recordEvent(
        sessionId: String,
        eventType: EventType,
        eventName: String,
        metadata: String?,
        screenshotPath: String?,
        memoryUsageMb: Long?,
        cpuUsagePercent: Float?
    ) {
        scope.launch {
            val event = EventEntity(
                sessionId = sessionId,
                eventType = eventType.name,
                eventName = eventName,
                timestamp = System.currentTimeMillis(),
                metadataJson = metadata,
                screenshotPath = screenshotPath,
                memoryUsageMb = memoryUsageMb,
                cpuUsagePercent = cpuUsagePercent
            )
            eventDao.insertEvent(event)
            sessionDao.incrementEventCount(sessionId)
            
            // Mark as crashed if it's a crash event
            if (eventType == EventType.CRASH) {
                sessionDao.markAsCrashed(sessionId)
            }
        }
    }

    suspend fun getEventsForSession(sessionId: String): List<EventEntity> {
        return eventDao.getEventsBySessionId(sessionId)
    }

    fun observeEventsForSession(sessionId: String): Flow<List<EventEntity>> {
        return eventDao.observeEventsBySessionId(sessionId)
    }

    suspend fun getRecentEvents(limit: Int = 50): List<EventEntity> {
        return eventDao.getRecentEvents(limit)
    }

    // Cleanup

    fun cleanupOldData(maxAgeDays: Int = 7) {
        scope.launch {
            val cutoffTime = System.currentTimeMillis() - (maxAgeDays * 24 * 60 * 60 * 1000L)
            eventDao.deleteOldEvents(cutoffTime)
            sessionDao.deleteOldSessions(cutoffTime)
        }
    }
}

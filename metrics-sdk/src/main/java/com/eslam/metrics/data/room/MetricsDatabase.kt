package com.eslam.metrics.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * MetricsDatabase - Room database for session and event persistence
 */
@Database(
    entities = [SessionEntity::class, EventEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
internal abstract class MetricsDatabase : RoomDatabase() {

    abstract fun sessionDao(): SessionDao
    abstract fun eventDao(): EventDao

    companion object {
        private const val DATABASE_NAME = "metrics_sdk_database"

        @Volatile
        private var INSTANCE: MetricsDatabase? = null

        fun getInstance(context: Context): MetricsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MetricsDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.pilltracker.app.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.pilltracker.app.data.converter.Converters
import com.pilltracker.app.data.dao.PillDao
import com.pilltracker.app.data.model.Pill

@Database(
    entities = [Pill::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PillDatabase : RoomDatabase() {
    abstract fun pillDao(): PillDao

    companion object {
        @Volatile
        private var INSTANCE: PillDatabase? = null

        fun getDatabase(context: Context): PillDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PillDatabase::class.java,
                    "pill_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
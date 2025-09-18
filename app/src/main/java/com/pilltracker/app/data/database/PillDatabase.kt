package com.pilltracker.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
}
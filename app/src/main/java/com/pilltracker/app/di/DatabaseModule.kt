package com.pilltracker.app.di

import android.content.Context
import androidx.room.Room
import com.pilltracker.app.data.dao.PillDao
import com.pilltracker.app.data.database.PillDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePillDatabase(@ApplicationContext context: Context): PillDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            PillDatabase::class.java,
            "pill_database"
        ).build()
    }

    @Provides
    fun providePillDao(database: PillDatabase): PillDao {
        return database.pillDao()
    }
}
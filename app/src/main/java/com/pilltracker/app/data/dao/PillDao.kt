package com.pilltracker.app.data.dao

import androidx.room.*
import com.pilltracker.app.data.model.Pill
import kotlinx.coroutines.flow.Flow

@Dao
interface PillDao {
    @Query("SELECT * FROM pills ORDER BY createdAt DESC")
    fun getAllPills(): Flow<List<Pill>>

    @Query("SELECT * FROM pills WHERE id = :id")
    suspend fun getPillById(id: Long): Pill?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPill(pill: Pill): Long

    @Update
    suspend fun updatePill(pill: Pill)

    @Delete
    suspend fun deletePill(pill: Pill)

    @Query("SELECT * FROM pills WHERE taken = 0 ORDER BY nextDose ASC")
    fun getUpcomingPills(): Flow<List<Pill>>

    @Query("SELECT COUNT(*) FROM pills WHERE taken = 1")
    fun getTakenCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM pills WHERE taken = 0")
    fun getPendingCount(): Flow<Int>
}
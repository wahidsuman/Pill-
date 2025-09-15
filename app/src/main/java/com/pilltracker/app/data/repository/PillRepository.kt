package com.pilltracker.app.data.repository

import com.pilltracker.app.data.dao.PillDao
import com.pilltracker.app.data.model.Pill
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PillRepository @Inject constructor(
    private val pillDao: PillDao
) {
    fun getAllPills(): Flow<List<Pill>> = pillDao.getAllPills()
    
    suspend fun getPillById(id: Long): Pill? = pillDao.getPillById(id)
    
    suspend fun insertPill(pill: Pill): Long = pillDao.insertPill(pill)
    
    suspend fun updatePill(pill: Pill) = pillDao.updatePill(pill)
    
    suspend fun deletePill(pill: Pill) = pillDao.deletePill(pill)
    
    fun getUpcomingPills(): Flow<List<Pill>> = pillDao.getUpcomingPills()
    
    fun getTakenCount(): Flow<Int> = pillDao.getTakenCount()
    
    fun getPendingCount(): Flow<Int> = pillDao.getPendingCount()
}
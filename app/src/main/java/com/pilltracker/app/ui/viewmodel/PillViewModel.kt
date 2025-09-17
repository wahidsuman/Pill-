package com.pilltracker.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pilltracker.app.data.model.Pill
import com.pilltracker.app.data.repository.PillRepository
import com.pilltracker.app.service.PillAlarmManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PillViewModel @Inject constructor(
    private val repository: PillRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val alarmManager = PillAlarmManager(context)

    private val _pills = MutableStateFlow<List<Pill>>(emptyList())
    val pills: StateFlow<List<Pill>> = _pills.asStateFlow()

    private val _upcomingPills = MutableStateFlow<List<Pill>>(emptyList())
    val upcomingPills: StateFlow<List<Pill>> = _upcomingPills.asStateFlow()

    private val _takenCount = MutableStateFlow(0)
    val takenCount: StateFlow<Int> = _takenCount.asStateFlow()

    private val _pendingCount = MutableStateFlow(0)
    val pendingCount: StateFlow<Int> = _pendingCount.asStateFlow()

    private val _showAddForm = MutableStateFlow(false)
    val showAddForm: StateFlow<Boolean> = _showAddForm.asStateFlow()

    private val _showEditForm = MutableStateFlow<Pill?>(null)
    val showEditForm: StateFlow<Pill?> = _showEditForm.asStateFlow()

    init {
        loadPills()
        // Don't schedule alarms immediately on startup to avoid crashes
        // Alarms will be scheduled when pills are added
    }

    private fun loadPills() {
        viewModelScope.launch {
            repository.getAllPills().collect { pillsList ->
                _pills.value = pillsList
            }
        }

        viewModelScope.launch {
            repository.getUpcomingPills().collect { upcomingList ->
                _upcomingPills.value = upcomingList
            }
        }

        viewModelScope.launch {
            repository.getTakenCount().collect { count ->
                _takenCount.value = count
            }
        }

        viewModelScope.launch {
            repository.getPendingCount().collect { count ->
                _pendingCount.value = count
            }
        }
    }

    fun addPill(pill: Pill) {
        viewModelScope.launch {
            try {
                val pillId = repository.insertPill(pill)
                val pillWithId = pill.copy(id = pillId)
                
                // Only schedule alarm if we have permission
                if (alarmManager.checkAlarmPermissions()) {
                    alarmManager.schedulePillReminder(pillWithId)
                    android.util.Log.d("PillViewModel", "Successfully scheduled alarm for pill: ${pill.name}")
                } else {
                    android.util.Log.w("PillViewModel", "Cannot schedule alarm - permission denied for pill: ${pill.name}")
                }
                
                _showAddForm.value = false
                android.util.Log.d("PillViewModel", "Successfully added pill: ${pill.name}")
            } catch (e: Exception) {
                android.util.Log.e("PillViewModel", "Error adding pill: ${e.message}", e)
                // Don't crash the app, just log the error
            }
        }
    }

    fun updatePill(pill: Pill) {
        viewModelScope.launch {
            try {
                repository.updatePill(pill)
                android.util.Log.d("PillViewModel", "Successfully updated pill: ${pill.name}")
            } catch (e: Exception) {
                android.util.Log.e("PillViewModel", "Error updating pill: ${e.message}", e)
            }
        }
    }

    fun deletePill(pill: Pill) {
        viewModelScope.launch {
            try {
                alarmManager.cancelPillReminder(pill)
                repository.deletePill(pill)
                android.util.Log.d("PillViewModel", "Successfully deleted pill: ${pill.name}")
            } catch (e: Exception) {
                android.util.Log.e("PillViewModel", "Error deleting pill: ${e.message}", e)
            }
        }
    }

    fun markAsTaken(pill: Pill) {
        val updatedPill = pill.copy(taken = !pill.taken)
        updatePill(updatedPill)
    }

    fun showAddForm() {
        _showAddForm.value = true
    }

    fun hideAddForm() {
        _showAddForm.value = false
    }

    fun showEditForm(pill: Pill) {
        _showEditForm.value = pill
    }

    fun hideEditForm() {
        _showEditForm.value = null
    }
}
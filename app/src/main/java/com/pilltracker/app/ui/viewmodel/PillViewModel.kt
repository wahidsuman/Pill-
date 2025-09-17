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
            try {
                repository.getAllPills().collect { pillsList ->
                    _pills.value = pillsList
                }
            } catch (e: Exception) {
                android.util.Log.e("PillViewModel", "Error loading pills: ${e.message}", e)
                _pills.value = emptyList()
            }
        }

        viewModelScope.launch {
            try {
                repository.getUpcomingPills().collect { upcomingList ->
                    _upcomingPills.value = upcomingList
                }
            } catch (e: Exception) {
                android.util.Log.e("PillViewModel", "Error loading upcoming pills: ${e.message}", e)
                _upcomingPills.value = emptyList()
            }
        }

        viewModelScope.launch {
            try {
                repository.getTakenCount().collect { count ->
                    _takenCount.value = count
                }
            } catch (e: Exception) {
                android.util.Log.e("PillViewModel", "Error loading taken count: ${e.message}", e)
                _takenCount.value = 0
            }
        }

        viewModelScope.launch {
            try {
                repository.getPendingCount().collect { count ->
                    _pendingCount.value = count
                }
            } catch (e: Exception) {
                android.util.Log.e("PillViewModel", "Error loading pending count: ${e.message}", e)
                _pendingCount.value = 0
            }
        }
    }

    fun addPill(pill: Pill) {
        viewModelScope.launch {
            try {
                // Validate pill data before adding
                if (pill.name.isBlank()) {
                    android.util.Log.e("PillViewModel", "Cannot add pill: name is blank")
                    return@launch
                }
                
                if (pill.times.isEmpty() || pill.times.all { it.isBlank() }) {
                    android.util.Log.e("PillViewModel", "Cannot add pill: no valid times provided")
                    return@launch
                }
                
                val pillId = repository.insertPill(pill)
                val pillWithId = pill.copy(id = pillId)
                
                // Only schedule alarm if we have permission
                try {
                    if (alarmManager.checkAlarmPermissions()) {
                        alarmManager.schedulePillReminder(pillWithId)
                        android.util.Log.d("PillViewModel", "Successfully scheduled alarm for pill: ${pill.name}")
                    } else {
                        android.util.Log.w("PillViewModel", "Cannot schedule alarm - permission denied for pill: ${pill.name}")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("PillViewModel", "Error scheduling alarm for pill: ${pill.name}", e)
                    // Continue even if alarm scheduling fails
                }
                
                _showAddForm.value = false
                android.util.Log.d("PillViewModel", "Successfully added pill: ${pill.name}")
            } catch (e: Exception) {
                android.util.Log.e("PillViewModel", "Error adding pill: ${e.message}", e)
                // Don't crash the app, just log the error
                _showAddForm.value = false // Hide the form even if there was an error
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
        try {
            _showAddForm.value = true
            android.util.Log.d("PillViewModel", "Showing add form")
        } catch (e: Exception) {
            android.util.Log.e("PillViewModel", "Error showing add form: ${e.message}", e)
        }
    }

    fun hideAddForm() {
        try {
            _showAddForm.value = false
            android.util.Log.d("PillViewModel", "Hiding add form")
        } catch (e: Exception) {
            android.util.Log.e("PillViewModel", "Error hiding add form: ${e.message}", e)
        }
    }

    fun showEditForm(pill: Pill) {
        _showEditForm.value = pill
    }

    fun hideEditForm() {
        _showEditForm.value = null
    }
}
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
        // Reschedule all alarms when the app starts
        viewModelScope.launch {
            repository.getAllPills().collect { pillsList ->
                if (pillsList.isNotEmpty()) {
                    alarmManager.scheduleAllPills(pillsList)
                }
            }
        }
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
            val pillId = repository.insertPill(pill)
            val pillWithId = pill.copy(id = pillId)
            alarmManager.schedulePillReminder(pillWithId)
            _showAddForm.value = false
        }
    }

    fun updatePill(pill: Pill) {
        viewModelScope.launch {
            repository.updatePill(pill)
        }
    }

    fun deletePill(pill: Pill) {
        viewModelScope.launch {
            alarmManager.cancelPillReminder(pill)
            repository.deletePill(pill)
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
    
    fun scheduleTestAlarm() {
        viewModelScope.launch {
            try {
                // Schedule alarm for 1 minute from now
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.MINUTE, 1)
                
                val testPill = Pill(
                    id = 999L,
                    name = "Test Medicine",
                    dosage = "1 tablet",
                    times = listOf("${calendar.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')}:${calendar.get(Calendar.MINUTE).toString().padStart(2, '0')}"),
                    color = "#FF5722",
                    imagePath = "",
                    nextDose = "Test",
                    taken = false
                )
                alarmManager.schedulePillReminder(testPill)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun testAlarmService() {
        viewModelScope.launch {
            try {
                // Test the alarm service directly with immediate alarm
                alarmManager.scheduleImmediateTest()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun rescheduleAllAlarms() {
        viewModelScope.launch {
            try {
                val pillsList = _pills.value
                if (pillsList.isNotEmpty()) {
                    alarmManager.scheduleAllPills(pillsList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
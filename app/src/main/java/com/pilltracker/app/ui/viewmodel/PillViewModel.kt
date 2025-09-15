package com.pilltracker.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pilltracker.app.data.model.Pill
import com.pilltracker.app.data.repository.PillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PillViewModel @Inject constructor(
    private val repository: PillRepository
) : ViewModel() {

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

    init {
        loadPills()
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
            repository.insertPill(pill)
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
}
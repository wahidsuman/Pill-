package com.pilltracker

import java.util.*

class MedicationRepository {
    private val medications = mutableListOf<Medication>()
    private val doses = mutableListOf<MedicationDose>()

    init {
        // Add sample data
        addSampleData()
    }

    private fun addSampleData() {
        val sampleMedication = Medication(
            name = "hdms",
            dosage = "1 tablet",
            frequency = MedicationFrequency.DAILY,
            times = listOf("14:02")
        )
        medications.add(sampleMedication)

        // Add sample dose
        val sampleDose = MedicationDose(
            medicationId = sampleMedication.id,
            scheduledTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 14)
                set(Calendar.MINUTE, 2)
            }.time
        )
        doses.add(sampleDose)
    }

    fun getAllMedications(): List<Medication> = medications.toList()

    fun getMedicationById(id: String): Medication? = medications.find { it.id == id }

    fun addMedication(medication: Medication) {
        medications.add(medication)
    }

    fun updateMedication(medication: Medication) {
        val index = medications.indexOfFirst { it.id == medication.id }
        if (index != -1) {
            medications[index] = medication
        }
    }

    fun deleteMedication(id: String) {
        medications.removeAll { it.id == id }
        doses.removeAll { it.medicationId == id }
    }

    fun getTodaysDoses(): List<MedicationDose> {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val tomorrow = Calendar.getInstance()
        tomorrow.time = today.time
        tomorrow.add(Calendar.DAY_OF_MONTH, 1)

        return doses.filter { dose ->
            dose.scheduledTime >= today.time && dose.scheduledTime < tomorrow.time
        }
    }

    fun getPendingDoses(): List<MedicationDose> = getTodaysDoses().filter { !it.isTaken }

    fun getTakenDoses(): List<MedicationDose> = getTodaysDoses().filter { it.isTaken }

    fun markDoseAsTaken(doseId: String) {
        val dose = doses.find { it.medicationId == doseId }
        dose?.let {
            val index = doses.indexOf(it)
            if (index != -1) {
                doses[index] = it.copy(
                    isTaken = true,
                    takenTime = Date()
                )
            }
        }
    }

    fun getNextReminder(): MedicationDose? {
        val now = Date()
        return getPendingDoses()
            .filter { it.scheduledTime > now }
            .minByOrNull { it.scheduledTime }
    }
}
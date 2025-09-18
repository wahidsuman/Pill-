package com.pilltracker

import java.util.*

data class Medication(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val dosage: String,
    val frequency: MedicationFrequency,
    val times: List<String>, // List of times like ["08:00", "14:00", "20:00"]
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val lastTaken: Date? = null
)

enum class MedicationFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    AS_NEEDED
}

data class MedicationDose(
    val medicationId: String,
    val scheduledTime: Date,
    val takenTime: Date? = null,
    val isTaken: Boolean = false,
    val notes: String? = null
)
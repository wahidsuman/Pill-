package com.pillreminder.app.data.model

data class Medication(
    val id: String = "",
    val name: String = "",
    val dosage: String = "",
    val reminderTime: String = "",
    val notes: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

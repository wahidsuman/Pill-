package com.pilltracker.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pills")
data class Pill(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val dosage: String = "1 tablet",
    val times: List<String> = listOf("08:00"), // JSON string of times
    val color: String = "blue",
    val imagePath: String = "",
    val nextDose: String = "08:00",
    val taken: Boolean = false,
    val frequency: String = "daily", // daily, weekly, monthly, custom
    val customDays: List<String> = emptyList(), // For custom frequency (e.g., ["monday", "wednesday", "friday"])
    val createdAt: Long = System.currentTimeMillis()
)
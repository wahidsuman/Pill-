package com.pilltracker.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(tableName = "pills")
data class Pill(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val dosage: String,
    val times: List<String>, // JSON string of times
    val color: String,
    val image: String? = null,
    val nextDose: String,
    val taken: Boolean = false,
    val frequency: String = "daily", // daily, weekly, monthly, custom
    val customDays: List<String> = emptyList(), // For custom frequency (e.g., ["monday", "wednesday", "friday"])
    val createdAt: Long = System.currentTimeMillis()
)
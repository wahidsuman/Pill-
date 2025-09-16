package com.pilltracker.app.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.pilltracker.app.data.model.Pill
import java.util.*

class PillAlarmManager(private val context: Context) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationService = PillNotificationService(context)
    
    fun schedulePillReminder(pill: Pill) {
        pill.times.forEach { timeString ->
            val time = parseTime(timeString)
            if (time != null) {
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, time.first)
                    set(Calendar.MINUTE, time.second)
                    set(Calendar.SECOND, 0)
                    
                    // If the time has already passed today, schedule for tomorrow
                    if (timeInMillis <= System.currentTimeMillis()) {
                        add(Calendar.DAY_OF_MONTH, 1)
                    }
                }
                
                // For now, just show notification directly instead of using alarm
                // This avoids the need for the receiver in manifest
                notificationService.showPillReminder(pill.name, pill.dosage, pill.id, pill.imagePath)
            }
        }
    }
    
    fun cancelPillReminder(pill: Pill) {
        pill.times.forEach { timeString ->
            val intent = Intent(context, PillReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                (pill.id + timeString.hashCode()).toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
    
    fun snoozePillReminder(pill: Pill, snoozeMinutes: Int = 5) {
        // For now, just show notification directly instead of using alarm
        notificationService.showPillReminder(pill.name, pill.dosage, pill.id, pill.imagePath)
    }
    
    fun rescheduleNextDay(pill: Pill, timeString: String) {
        // For now, just show notification directly instead of using alarm
        notificationService.showPillReminder(pill.name, pill.dosage, pill.id, pill.imagePath)
    }
    
    fun scheduleImmediateTest() {
        // For now, just show notification directly instead of using alarm
        notificationService.showPillReminder("Test Medicine", "1 tablet", 999L, "")
    }
    
    private fun parseTime(timeString: String): Pair<Int, Int>? {
        return try {
            val parts = timeString.split(":")
            if (parts.size == 2) {
                val hour = parts[0].toInt()
                val minute = parts[1].toInt()
                if (hour in 0..23 && minute in 0..59) {
                    Pair(hour, minute)
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
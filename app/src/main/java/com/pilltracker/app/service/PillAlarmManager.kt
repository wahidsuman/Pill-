package com.pilltracker.app.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
                
                val intent = Intent(context, PillReminderReceiver::class.java).apply {
                    putExtra("pill_name", pill.name)
                    putExtra("pill_dosage", pill.dosage)
                    putExtra("pill_id", pill.id)
                    putExtra("pill_time", timeString)
                    putExtra("pill_image_path", pill.imagePath)
                }
                
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    (pill.id + timeString.hashCode()).toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
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
        val snoozeTime = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000)
        
        val intent = Intent(context, PillReminderReceiver::class.java).apply {
            putExtra("pill_name", pill.name)
            putExtra("pill_dosage", pill.dosage)
            putExtra("pill_id", pill.id)
            putExtra("pill_time", "snooze")
            putExtra("pill_image_path", pill.imagePath)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (pill.id + "snooze".hashCode()).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            snoozeTime,
            pendingIntent
        )
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
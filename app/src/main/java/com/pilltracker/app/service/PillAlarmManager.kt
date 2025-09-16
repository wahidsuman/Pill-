package com.pilltracker.app.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.pilltracker.app.data.model.Pill
import java.util.*

class PillAlarmManager(private val context: Context) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationService = PillNotificationService(context)
    
    fun schedulePillReminder(pill: Pill) {
        Log.d("PillAlarmManager", "Scheduling alarm for pill: ${pill.name}")
        
        pill.times.forEach { timeString ->
            val time = parseTime(timeString)
            if (time != null) {
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, time.first)
                    set(Calendar.MINUTE, time.second)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    
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
                
                // Schedule the alarm with proper method for Android version
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // For Android 6.0+ use setExactAndAllowWhileIdle for better reliability
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                        Log.d("PillAlarmManager", "Scheduled exact alarm for ${pill.name} at ${timeString} for ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(calendar.time)}")
                    } else {
                        // For older versions use setExact
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                        Log.d("PillAlarmManager", "Scheduled exact alarm for ${pill.name} at ${timeString} for ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(calendar.time)}")
                    }
                } catch (e: Exception) {
                    Log.e("PillAlarmManager", "Failed to schedule exact alarm, trying fallback", e)
                    try {
                        // Fallback to regular set
                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                        Log.d("PillAlarmManager", "Scheduled fallback alarm for ${pill.name} at ${timeString} for ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(calendar.time)}")
                    } catch (e2: Exception) {
                        Log.e("PillAlarmManager", "Failed to schedule any alarm for ${pill.name}", e2)
                    }
                }
            } else {
                Log.e("PillAlarmManager", "Invalid time format: $timeString")
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
        Log.d("PillAlarmManager", "Scheduling snooze alarm for ${pill.name} in $snoozeMinutes minutes")
        
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
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    snoozeTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    snoozeTime,
                    pendingIntent
                )
            }
            Log.d("PillAlarmManager", "Snooze alarm scheduled successfully")
        } catch (e: Exception) {
            Log.e("PillAlarmManager", "Failed to schedule snooze alarm", e)
        }
    }
    
    fun rescheduleNextDay(pill: Pill, timeString: String) {
        val time = parseTime(timeString) ?: return
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, time.first)
            set(Calendar.MINUTE, time.second)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        Log.d("PillAlarmManager", "Rescheduling alarm for ${pill.name} for next day at $timeString")
        
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
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            Log.d("PillAlarmManager", "Next day alarm scheduled successfully")
        } catch (e: Exception) {
            Log.e("PillAlarmManager", "Failed to reschedule next day alarm", e)
        }
    }
    
    fun scheduleImmediateTest() {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.SECOND, 5) // 5 seconds from now
            set(Calendar.MILLISECOND, 0)
        }
        
        Log.d("PillAlarmManager", "Scheduling immediate test alarm")
        
        val intent = Intent(context, PillReminderReceiver::class.java).apply {
            putExtra("pill_name", "Test Medicine")
            putExtra("pill_dosage", "1 tablet")
            putExtra("pill_id", 999L)
            putExtra("pill_time", "test")
            putExtra("pill_image_path", "")
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            Log.d("PillAlarmManager", "Test alarm scheduled successfully")
        } catch (e: Exception) {
            Log.e("PillAlarmManager", "Failed to schedule test alarm", e)
        }
    }
    
    fun scheduleAllPills(pills: List<Pill>) {
        Log.d("PillAlarmManager", "Scheduling alarms for ${pills.size} pills")
        pills.forEach { pill ->
            schedulePillReminder(pill)
        }
    }
    
    fun scheduleAlarmForTomorrow(pill: Pill, timeString: String) {
        val time = parseTime(timeString) ?: return
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, time.first)
            set(Calendar.MINUTE, time.second)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        Log.d("PillAlarmManager", "Scheduling tomorrow's alarm for ${pill.name} at $timeString")
        
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
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            Log.d("PillAlarmManager", "Tomorrow's alarm scheduled successfully for ${pill.name}")
        } catch (e: Exception) {
            Log.e("PillAlarmManager", "Failed to schedule tomorrow's alarm for ${pill.name}", e)
        }
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
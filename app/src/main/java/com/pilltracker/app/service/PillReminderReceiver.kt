package com.pilltracker.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.pilltracker.app.data.model.Pill

class PillReminderReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("PillReminderReceiver", "Alarm received!")
        
        val pillName = intent.getStringExtra("pill_name") ?: return
        val pillDosage = intent.getStringExtra("pill_dosage") ?: return
        val pillId = intent.getLongExtra("pill_id", -1)
        val imagePath = intent.getStringExtra("pill_image_path") ?: ""
        val timeString = intent.getStringExtra("pill_time") ?: ""
        
        Log.d("PillReminderReceiver", "Pill: $pillName, Dosage: $pillDosage, ID: $pillId")
        
        if (pillId != -1L) {
            // Start the alarm service with actual alarm sound
            val alarmServiceIntent = Intent(context, AlarmService::class.java).apply {
                action = AlarmService.ACTION_START_ALARM
                putExtra(AlarmService.EXTRA_PILL_NAME, pillName)
                putExtra(AlarmService.EXTRA_PILL_DOSAGE, pillDosage)
                putExtra(AlarmService.EXTRA_PILL_ID, pillId)
                putExtra(AlarmService.EXTRA_PILL_TIME, timeString)
                putExtra(AlarmService.EXTRA_PILL_IMAGE_PATH, imagePath)
            }
            
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(alarmServiceIntent)
                } else {
                    context.startService(alarmServiceIntent)
                }
                Log.d("PillReminderReceiver", "Started AlarmService")
            } catch (e: Exception) {
                Log.e("PillReminderReceiver", "Failed to start AlarmService", e)
                // Fallback to notification only
                val notificationService = PillNotificationService(context)
                notificationService.showPillReminder(pillName, pillDosage, pillId, imagePath)
            }
            
            // Reschedule for next day if this is not a snooze or test
            if (timeString != "snooze" && timeString != "test") {
                // Use a background thread to reschedule the alarm
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    try {
                        val alarmManager = PillAlarmManager(context)
                        val pill = Pill(
                            id = pillId,
                            name = pillName,
                            dosage = pillDosage,
                            times = listOf(timeString),
                            color = "",
                            imagePath = imagePath,
                            nextDose = "",
                            taken = false
                        )
                        alarmManager.scheduleAlarmForTomorrow(pill, timeString)
                        Log.d("PillReminderReceiver", "Rescheduled alarm for ${pillName} at $timeString")
                    } catch (e: Exception) {
                        Log.e("PillReminderReceiver", "Failed to reschedule alarm for ${pillName}", e)
                    }
                }, 1000) // Delay by 1 second to ensure the alarm service has started
            }
        }
    }
}
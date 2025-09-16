package com.pilltracker.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pilltracker.app.data.model.Pill

class PillReminderReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val pillName = intent.getStringExtra("pill_name") ?: return
        val pillDosage = intent.getStringExtra("pill_dosage") ?: return
        val pillId = intent.getLongExtra("pill_id", -1)
        val imagePath = intent.getStringExtra("pill_image_path") ?: ""
        val timeString = intent.getStringExtra("pill_time") ?: ""
        
        if (pillId != -1L) {
            val notificationService = PillNotificationService(context)
            notificationService.showPillReminder(pillName, pillDosage, pillId, imagePath)
            
            // Reschedule for next day if this is not a snooze
            if (timeString != "snooze") {
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
                alarmManager.rescheduleNextDay(pill, timeString)
            }
        }
    }
}
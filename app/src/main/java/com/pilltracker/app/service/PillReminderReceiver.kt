package com.pilltracker.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PillReminderReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val pillName = intent.getStringExtra("pill_name") ?: return
        val pillDosage = intent.getStringExtra("pill_dosage") ?: return
        val pillId = intent.getLongExtra("pill_id", -1)
        val imagePath = intent.getStringExtra("pill_image_path") ?: ""
        
        if (pillId != -1L) {
            val notificationService = PillNotificationService(context)
            notificationService.showPillReminder(pillName, pillDosage, pillId, imagePath)
        }
    }
}
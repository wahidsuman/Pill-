package com.pilltracker.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.pilltracker.app.MainActivity

class PillNotificationService(private val context: Context) {
    
    companion object {
        const val CHANNEL_ID = "pill_reminder_channel"
        const val NOTIFICATION_ID_BASE = 1000
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Pill Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for pill reminders"
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showPillReminder(pillName: String, dosage: String, pillId: Long, imagePath: String = "") {
        // Just show notification for now - no popup activity
        // imagePath parameter kept for compatibility but not used
        
        // Also show a regular notification as backup
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            pillId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("💊 Time to take your medication!")
            .setContentText("$pillName - $dosage")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("It's time to take your $pillName ($dosage). Don't forget to mark it as taken in the app!")
            )
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            if (areNotificationsEnabled()) {
                try {
                    notify((NOTIFICATION_ID_BASE + pillId).toInt(), notification)
                } catch (e: SecurityException) {
                    // Handle case where notification permission is revoked
                    android.util.Log.e("PillNotificationService", "Failed to show notification: ${e.message}")
                }
            }
        }
    }
    
    fun cancelPillReminder(pillId: Long) {
        with(NotificationManagerCompat.from(context)) {
            try {
                cancel((NOTIFICATION_ID_BASE + pillId).toInt())
            } catch (e: SecurityException) {
                // Handle case where notification permission is revoked
                android.util.Log.e("PillNotificationService", "Failed to cancel notification: ${e.message}")
            }
        }
    }
    
    fun testPopup() {
        showPillReminder("Test Medicine", "1 tablet", 999L, "")
    }
    
    private fun areNotificationsEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }
}
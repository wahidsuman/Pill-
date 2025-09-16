package com.pilltracker.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pilltracker.app.MainActivity
import com.pilltracker.app.R
import com.pilltracker.app.ui.screen.PillReminderPopupActivity

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
        try {
            // Launch the popup activity
            val popupIntent = Intent(context, PillReminderPopupActivity::class.java).apply {
                putExtra("pill_name", pillName)
                putExtra("pill_dosage", dosage)
                putExtra("pill_id", pillId)
                putExtra("pill_image_path", imagePath)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            
            context.startActivity(popupIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: just show notification if popup fails
        }
        
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
            .setSmallIcon(R.drawable.ic_launcher_foreground)
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
            notify((NOTIFICATION_ID_BASE + pillId).toInt(), notification)
        }
    }
    
    fun cancelPillReminder(pillId: Long) {
        with(NotificationManagerCompat.from(context)) {
            cancel((NOTIFICATION_ID_BASE + pillId).toInt())
        }
    }
    
    fun testPopup() {
        showPillReminder("Test Medicine", "1 tablet", 999L, "")
    }
}
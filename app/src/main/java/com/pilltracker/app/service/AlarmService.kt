package com.pilltracker.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.pilltracker.app.MainActivity
import com.pilltracker.app.R

class AlarmService : Service() {
    
    companion object {
        const val ACTION_START_ALARM = "START_ALARM"
        const val ACTION_STOP_ALARM = "STOP_ALARM"
        const val ACTION_SNOOZE_ALARM = "SNOOZE_ALARM"
        const val CHANNEL_ID = "alarm_service_channel"
        const val NOTIFICATION_ID = 2000
        
        const val EXTRA_PILL_NAME = "pill_name"
        const val EXTRA_PILL_DOSAGE = "pill_dosage"
        const val EXTRA_PILL_ID = "pill_id"
        const val EXTRA_PILL_TIME = "pill_time"
        const val EXTRA_PILL_IMAGE_PATH = "pill_image_path"
    }
    
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var isAlarmPlaying = false
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        setupVibrator()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_ALARM -> {
                val pillName = intent.getStringExtra(EXTRA_PILL_NAME) ?: "Medication"
                val pillDosage = intent.getStringExtra(EXTRA_PILL_DOSAGE) ?: ""
                val pillId = intent.getLongExtra(EXTRA_PILL_ID, -1)
                val pillTime = intent.getStringExtra(EXTRA_PILL_TIME) ?: ""
                val imagePath = intent.getStringExtra(EXTRA_PILL_IMAGE_PATH) ?: ""
                
                startAlarm(pillName, pillDosage, pillId, pillTime, imagePath)
            }
            ACTION_STOP_ALARM -> {
                stopAlarm()
            }
            ACTION_SNOOZE_ALARM -> {
                val pillName = intent.getStringExtra(EXTRA_PILL_NAME) ?: "Medication"
                val pillDosage = intent.getStringExtra(EXTRA_PILL_DOSAGE) ?: ""
                val pillId = intent.getLongExtra(EXTRA_PILL_ID, -1)
                val pillTime = intent.getStringExtra(EXTRA_PILL_TIME) ?: ""
                val imagePath = intent.getStringExtra(EXTRA_PILL_IMAGE_PATH) ?: ""
                
                snoozeAlarm(pillName, pillDosage, pillId, pillTime, imagePath)
            }
        }
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Service",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Service for playing pill reminder alarms"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun setupVibrator() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    
    private fun startAlarm(pillName: String, pillDosage: String, pillId: Long, pillTime: String, imagePath: String) {
        if (isAlarmPlaying) return
        
        Log.d("AlarmService", "Starting alarm for: $pillName")
        
        // Start foreground service
        startForeground(NOTIFICATION_ID, createAlarmNotification(pillName, pillDosage, pillId, pillTime, imagePath))
        
        // Play alarm sound
        playAlarmSound()
        
        // Start vibration
        startVibration()
        
        isAlarmPlaying = true
    }
    
    private fun playAlarmSound() {
        try {
            // Get the default alarm ringtone URI
            val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                        .build()
                )
                
                setDataSource(this@AlarmService, alarmUri)
                isLooping = true
                setOnPreparedListener { mp ->
                    mp.start()
                    Log.d("AlarmService", "Alarm sound started")
                }
                setOnErrorListener { _, _, _ ->
                    Log.e("AlarmService", "Error playing alarm sound")
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("AlarmService", "Failed to play alarm sound", e)
        }
    }
    
    private fun startVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pattern = longArrayOf(0, 1000, 500, 1000, 500, 1000)
                val vibrationEffect = VibrationEffect.createWaveform(pattern, 0)
                vibrator?.vibrate(vibrationEffect)
            } else {
                @Suppress("DEPRECATION")
                val pattern = longArrayOf(0, 1000, 500, 1000, 500, 1000)
                vibrator?.vibrate(pattern, 0)
            }
        } catch (e: Exception) {
            Log.e("AlarmService", "Failed to start vibration", e)
        }
    }
    
    private fun stopAlarm() {
        Log.d("AlarmService", "Stopping alarm")
        
        // Stop media player
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.stop()
            }
            mp.release()
        }
        mediaPlayer = null
        
        // Stop vibration
        vibrator?.cancel()
        
        // Stop foreground service
        stopForeground(true)
        stopSelf()
        
        isAlarmPlaying = false
    }
    
    private fun snoozeAlarm(pillName: String, pillDosage: String, pillId: Long, pillTime: String, imagePath: String) {
        Log.d("AlarmService", "Snoozing alarm for: $pillName")
        
        // Stop current alarm
        stopAlarm()
        
        // Schedule snooze alarm
        val alarmManager = PillAlarmManager(this)
        val pill = com.pilltracker.app.data.model.Pill(
            id = pillId,
            name = pillName,
            dosage = pillDosage,
            times = listOf(pillTime),
            color = "",
            imagePath = imagePath,
            nextDose = "",
            taken = false
        )
        alarmManager.snoozePillReminder(pill, 5) // 5 minutes snooze
    }
    
    private fun createAlarmNotification(pillName: String, pillDosage: String, pillId: Long, pillTime: String, imagePath: String): android.app.Notification {
        // Intent for stopping the alarm
        val stopIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_STOP_ALARM
            putExtra(EXTRA_PILL_NAME, pillName)
            putExtra(EXTRA_PILL_DOSAGE, pillDosage)
            putExtra(EXTRA_PILL_ID, pillId)
            putExtra(EXTRA_PILL_TIME, pillTime)
            putExtra(EXTRA_PILL_IMAGE_PATH, imagePath)
        }
        val stopPendingIntent = PendingIntent.getService(
            this, pillId.toInt(), stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Intent for snoozing the alarm
        val snoozeIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_SNOOZE_ALARM
            putExtra(EXTRA_PILL_NAME, pillName)
            putExtra(EXTRA_PILL_DOSAGE, pillDosage)
            putExtra(EXTRA_PILL_ID, pillId)
            putExtra(EXTRA_PILL_TIME, pillTime)
            putExtra(EXTRA_PILL_IMAGE_PATH, imagePath)
        }
        val snoozePendingIntent = PendingIntent.getService(
            this, (pillId + 1000).toInt(), snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Intent for opening the app
        val appIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val appPendingIntent = PendingIntent.getActivity(
            this, (pillId + 2000).toInt(), appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🔔 Pill Reminder Alarm")
            .setContentText("Time to take: $pillName ($pillDosage)")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(true)
            .setContentIntent(appPendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Stop",
                stopPendingIntent
            )
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Snooze (5min)",
                snoozePendingIntent
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("🔔 ALARM: It's time to take your $pillName ($pillDosage). Tap 'Stop' to dismiss or 'Snooze' to be reminded again in 5 minutes.")
            )
            .build()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()
    }
}
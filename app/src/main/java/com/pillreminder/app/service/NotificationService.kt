package com.pillreminder.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class NotificationService : Service() {
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO: Handle notification service logic
        return START_STICKY
    }
}
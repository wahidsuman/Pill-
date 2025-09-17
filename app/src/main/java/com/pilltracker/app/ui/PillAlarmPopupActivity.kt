package com.pilltracker.app.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pilltracker.app.service.AlarmService
import com.pilltracker.app.ui.theme.PillTrackerTheme
import java.io.File

class PillAlarmPopupActivity : ComponentActivity() {
    
    companion object {
        const val EXTRA_PILL_NAME = "pill_name"
        const val EXTRA_PILL_DOSAGE = "pill_dosage"
        const val EXTRA_PILL_ID = "pill_id"
        const val EXTRA_PILL_TIME = "pill_time"
        const val EXTRA_PILL_IMAGE_PATH = "pill_image_path"
        const val EXTRA_PILL_COLOR = "pill_color"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make the activity appear as a popup overlay
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        
        val pillName = intent.getStringExtra(EXTRA_PILL_NAME) ?: "Medication"
        val pillDosage = intent.getStringExtra(EXTRA_PILL_DOSAGE) ?: ""
        val pillId = intent.getLongExtra(EXTRA_PILL_ID, -1)
        val pillTime = intent.getStringExtra(EXTRA_PILL_TIME) ?: ""
        val imagePath = intent.getStringExtra(EXTRA_PILL_IMAGE_PATH) ?: ""
        val pillColor = intent.getStringExtra(EXTRA_PILL_COLOR) ?: "blue"
        
        setContent {
            PillTrackerTheme {
                PillAlarmPopup(
                    pillName = pillName,
                    pillDosage = pillDosage,
                    pillId = pillId,
                    pillTime = pillTime,
                    imagePath = imagePath,
                    pillColor = pillColor,
                    onDismiss = { finish() },
                    onStopAlarm = { stopAlarm() },
                    onSnoozeAlarm = { snoozeAlarm() }
                )
            }
        }
    }
    
    private fun stopAlarm() {
        val stopIntent = Intent(this, AlarmService::class.java).apply {
            action = AlarmService.ACTION_STOP_ALARM
            putExtra(EXTRA_PILL_NAME, intent.getStringExtra(EXTRA_PILL_NAME))
            putExtra(EXTRA_PILL_DOSAGE, intent.getStringExtra(EXTRA_PILL_DOSAGE))
            putExtra(EXTRA_PILL_ID, intent.getLongExtra(EXTRA_PILL_ID, -1))
            putExtra(EXTRA_PILL_TIME, intent.getStringExtra(EXTRA_PILL_TIME))
            putExtra(EXTRA_PILL_IMAGE_PATH, intent.getStringExtra(EXTRA_PILL_IMAGE_PATH))
        }
        startService(stopIntent)
        finish()
    }
    
    private fun snoozeAlarm() {
        val snoozeIntent = Intent(this, AlarmService::class.java).apply {
            action = AlarmService.ACTION_SNOOZE_ALARM
            putExtra(EXTRA_PILL_NAME, intent.getStringExtra(EXTRA_PILL_NAME))
            putExtra(EXTRA_PILL_DOSAGE, intent.getStringExtra(EXTRA_PILL_DOSAGE))
            putExtra(EXTRA_PILL_ID, intent.getLongExtra(EXTRA_PILL_ID, -1))
            putExtra(EXTRA_PILL_TIME, intent.getStringExtra(EXTRA_PILL_TIME))
            putExtra(EXTRA_PILL_IMAGE_PATH, intent.getStringExtra(EXTRA_PILL_IMAGE_PATH))
        }
        startService(snoozeIntent)
        finish()
    }
}

@Composable
fun PillAlarmPopup(
    pillName: String,
    pillDosage: String,
    pillId: Long,
    pillTime: String,
    imagePath: String,
    pillColor: String,
    onDismiss: () -> Unit,
    onStopAlarm: () -> Unit,
    onSnoozeAlarm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Alarm icon
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = "Alarm",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp),
                    tint = Color(0xFFFF6B6B)
                )
                
                // Title
                Text(
                    text = "🔔 PILL REMINDER",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Time
                Text(
                    text = "Time: $pillTime",
                    fontSize = 16.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Medicine image or color circle
                if (imagePath.isNotEmpty()) {
                    // Show medicine image
                    val bitmap = remember(imagePath) {
                        try {
                            BitmapFactory.decodeFile(imagePath)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Medicine image",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Fallback to color circle
                        ColorCircle(color = pillColor, size = 120.dp)
                    }
                } else {
                    // Show color circle
                    ColorCircle(color = pillColor, size = 120.dp)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Medicine name
                Text(
                    text = pillName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C2C2C),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Dosage if available
                if (pillDosage.isNotEmpty()) {
                    Text(
                        text = "Dosage: $pillDosage",
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Snooze button
                    OutlinedButton(
                        onClick = onSnoozeAlarm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF666666)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Snooze,
                            contentDescription = "Snooze",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Snooze 5min")
                    }
                    
                    // Stop button
                    Button(
                        onClick = onStopAlarm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Taken",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Taken")
                    }
                }
            }
        }
    }
}

@Composable
fun ColorCircle(
    color: String,
    size: androidx.compose.ui.unit.Dp
) {
    val colorValue = when (color) {
        "blue" -> Color(0xFF2196F3)
        "red" -> Color(0xFFF44336)
        "green" -> Color(0xFF4CAF50)
        "orange" -> Color(0xFFFF9800)
        "purple" -> Color(0xFF9C27B0)
        else -> Color(0xFF2196F3)
    }
    
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(colorValue),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Medication,
            contentDescription = "Medicine",
            tint = Color.White,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}
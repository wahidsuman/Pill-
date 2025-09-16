package com.pilltracker.app.ui.screen

import android.app.KeyguardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pilltracker.app.data.model.Pill
import com.pilltracker.app.service.PillAlarmManager
import com.pilltracker.app.ui.theme.PillTrackerTheme
import com.pilltracker.app.ui.viewmodel.PillViewModel
import kotlinx.coroutines.delay
import java.io.File

class PillReminderPopupActivity : ComponentActivity() {
    
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make the activity visible and keep screen on
        try {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Make activity fullscreen
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        
        // Get data from intent
        val pillName = intent.getStringExtra("pill_name") ?: "Medication"
        val pillDosage = intent.getStringExtra("pill_dosage") ?: ""
        val pillId = intent.getLongExtra("pill_id", -1)
        val imagePath = intent.getStringExtra("pill_image_path") ?: ""
        
        // Initialize vibration
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        // Start alarm sound and vibration
        startAlarm()
        
        setContent {
            PillTrackerTheme {
                val viewModel: PillViewModel = viewModel()
                val alarmManager = PillAlarmManager(this)
                
                PillReminderPopup(
                    pillName = pillName,
                    pillDosage = pillDosage,
                    imagePath = imagePath,
                    onDismiss = { 
                        // Snooze for 5 minutes
                        alarmManager.snoozePillReminder(
                            Pill(
                                id = pillId,
                                name = pillName,
                                dosage = pillDosage,
                                times = emptyList(),
                                color = "",
                                imagePath = imagePath,
                                nextDose = "",
                                taken = false
                            ),
                            5
                        )
                        finish() 
                    },
                    onMarkTaken = { 
                        // Mark pill as taken in database
                        viewModel.markAsTaken(
                            Pill(
                                id = pillId,
                                name = pillName,
                                dosage = pillDosage,
                                times = emptyList(),
                                color = "",
                                imagePath = imagePath,
                                nextDose = "",
                                taken = true
                            )
                        )
                        finish() 
                    }
                )
            }
        }
    }
    
    private fun startAlarm() {
        // Play alarm sound
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(this@PillReminderPopupActivity, alarmUri)
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Start vibration pattern
        vibrator?.let { vib ->
            val pattern = longArrayOf(0, 1000, 500, 1000, 500, 1000)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect = VibrationEffect.createWaveform(pattern, 0)
                vib.vibrate(vibrationEffect)
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(pattern, 0)
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        vibrator?.cancel()
    }
    
    override fun onBackPressed() {
        // Prevent back button from dismissing the popup
        // User must use the buttons in the popup
    }
}

@Composable
fun PillReminderPopup(
    pillName: String,
    pillDosage: String,
    imagePath: String,
    onDismiss: () -> Unit,
    onMarkTaken: () -> Unit
) {
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Medicine image
                if (imagePath.isNotEmpty() && File(imagePath).exists()) {
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
                            contentDescription = "Medicine Image",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        Text(
                            text = "💊",
                            fontSize = 60.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    Text(
                        text = "💊",
                        fontSize = 60.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Title
                Text(
                    text = "Time to take your medication!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Medicine name
                Text(
                    text = pillName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                
                // Dosage
                if (pillDosage.isNotEmpty()) {
                    Text(
                        text = "Dosage: $pillDosage",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Snooze")
                    }
                    
                    Button(
                        onClick = onMarkTaken,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Mark as Taken")
                    }
                }
            }
        }
    }
}
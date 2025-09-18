package com.pilltracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

// Temporarily remove @AndroidEntryPoint to test without Hilt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
            setContent {
                // Use default Material3 theme instead of custom theme
                androidx.compose.material3.MaterialTheme {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            androidx.compose.material3.Text(
                                text = "Pill Tracker - Working!",
                                fontSize = 24.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                            
                            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                            
                            androidx.compose.material3.Text(
                                text = "App is running successfully",
                                fontSize = 16.sp
                            )
                            
                            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                            
                            androidx.compose.material3.Button(
                                onClick = { 
                                    android.util.Log.d("MainActivity", "Button clicked - App is working!")
                                }
                            ) {
                                androidx.compose.material3.Text("Test Button")
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error in onCreate: ${e.message}", e)
            // If everything fails, show a simple error message
            setContentView(android.widget.TextView(this).apply {
                text = "App failed to load. Please restart."
                setTextColor(android.graphics.Color.RED)
                gravity = android.view.Gravity.CENTER
            })
        }
    }
}
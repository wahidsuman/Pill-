package com.pilltracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pilltracker.app.ui.screens.HomeScreen

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
                        modifier = Modifier.fillMaxSize()
                    ) {
                        HomeScreen()
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
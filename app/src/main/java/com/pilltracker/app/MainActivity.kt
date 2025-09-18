package com.pilltracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.pilltracker.app.ui.screens.HomeScreen
import com.pilltracker.app.ui.theme.PillTrackerTheme
import com.pilltracker.app.ui.viewmodel.PillViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
            setContent {
                PillTrackerTheme {
                    // Step 2: Add ViewModel back with minimal functionality
                    val pillViewModel: PillViewModel = hiltViewModel()
                    
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            androidx.compose.material3.Text(
                                text = "Pill Tracker",
                                fontSize = 24.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = androidx.compose.ui.graphics.Color.White
                            )
                            
                            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                            
                            androidx.compose.material3.Button(
                                onClick = { 
                                    android.util.Log.d("MainActivity", "Button clicked - ViewModel loaded successfully!")
                                }
                            ) {
                                androidx.compose.material3.Text("Test ViewModel")
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
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
                    try {
                        val pillViewModel: PillViewModel = hiltViewModel()
                        
                        Scaffold { paddingValues ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                            ) {
                                HomeScreen(viewModel = pillViewModel)
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MainActivity", "Error in Compose content: ${e.message}", e)
                        // Fallback UI in case of errors
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            androidx.compose.material3.Text(
                                text = "Error loading app. Please restart.",
                                color = androidx.compose.ui.graphics.Color.Red
                            )
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
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
        enableEdgeToEdge()
        setContent {
            PillTrackerTheme {
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
            }
        }
    }
}
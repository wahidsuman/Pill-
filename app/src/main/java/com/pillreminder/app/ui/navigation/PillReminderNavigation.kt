package com.pillreminder.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pillreminder.app.ui.screens.HomeScreen

@Composable
fun PillReminderNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }
        
        // TODO: Add more navigation routes
        // composable("add_medication") { ... }
        // composable("medication_details/{id}") { ... }
        // composable("settings") { ... }
    }
}
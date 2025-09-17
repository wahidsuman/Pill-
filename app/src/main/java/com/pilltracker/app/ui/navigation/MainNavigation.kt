package com.pilltracker.app.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pilltracker.app.ui.screens.*
import com.pilltracker.app.ui.viewmodel.PillViewModel

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Stats : Screen("stats", "Stats", Icons.Default.Analytics)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarToday)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    val pillViewModel: PillViewModel = hiltViewModel()
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(80.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                listOf(
                    Screen.Home,
                    Screen.Stats,
                    Screen.Calendar,
                    Screen.Settings
                ).forEach { screen ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = { 
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        selected = selectedScreen == screen,
                        onClick = { selectedScreen = screen }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedScreen) {
                Screen.Home -> HomeScreen(viewModel = pillViewModel)
                Screen.Stats -> StatsScreen()
                Screen.Calendar -> CalendarScreen()
                Screen.Settings -> SettingsScreen()
            }
        }
    }
}
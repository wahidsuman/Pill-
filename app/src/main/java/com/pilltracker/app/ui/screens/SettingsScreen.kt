package com.pilltracker.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pilltracker.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var showAlarmSettings by remember { mutableStateOf(false) }
    var showNotificationSettings by remember { mutableStateOf(false) }
    var showAppPreferences by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Settings",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Gray800,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Alarm Settings
        SettingsSection(
            title = "Alarm Settings",
            icon = Icons.Default.NotificationsActive,
            onClick = { showAlarmSettings = true }
        )
        
        // Notification Settings
        SettingsSection(
            title = "Notification Settings",
            icon = Icons.Default.Notifications,
            onClick = { showNotificationSettings = true }
        )
        
        // App Preferences
        SettingsSection(
            title = "App Preferences",
            icon = Icons.Default.Palette,
            onClick = { showAppPreferences = true }
        )
        
        // Data & Privacy
        SettingsSection(
            title = "Data & Privacy",
            icon = Icons.Default.Security,
            onClick = { /* TODO: Implement data settings */ }
        )
        
        // About
        SettingsSection(
            title = "About",
            icon = Icons.Default.Info,
            onClick = { /* TODO: Implement about */ }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
    
    // Alarm Settings Dialog
    if (showAlarmSettings) {
        AlarmSettingsDialog(
            onDismiss = { showAlarmSettings = false }
        )
    }
    
    // Notification Settings Dialog
    if (showNotificationSettings) {
        NotificationSettingsDialog(
            onDismiss = { showNotificationSettings = false }
        )
    }
    
    // App Preferences Dialog
    if (showAppPreferences) {
        AppPreferencesDialog(
            onDismiss = { showAppPreferences = false }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Blue600
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Gray800,
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                modifier = Modifier.size(20.dp),
                tint = Gray400
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmSettingsDialog(
    onDismiss: () -> Unit
) {
    var alarmSound by remember { mutableStateOf("Default") }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var snoozeDuration by remember { mutableStateOf(5) }
    var popupAlarmEnabled by remember { mutableStateOf(true) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Alarm Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray800,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // Alarm Sound
                Text(
                    text = "Alarm Sound",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray700,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                var expanded by remember { mutableStateOf(false) }
                val sounds = listOf("Default", "Gentle", "Loud", "Custom")
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = alarmSound,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        sounds.forEach { sound ->
                            DropdownMenuItem(
                                text = { Text(sound) },
                                onClick = {
                                    alarmSound = sound
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Vibration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vibration",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Gray700
                    )
                    Switch(
                        checked = vibrationEnabled,
                        onCheckedChange = { vibrationEnabled = it }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Popup Alarm
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Popup Alarm",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Gray700
                    )
                    Switch(
                        checked = popupAlarmEnabled,
                        onCheckedChange = { popupAlarmEnabled = it }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Snooze Duration
                Text(
                    text = "Snooze Duration: ${snoozeDuration} minutes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray700,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Slider(
                    value = snoozeDuration.toFloat(),
                    onValueChange = { snoozeDuration = it.toInt() },
                    valueRange = 5f..30f,
                    steps = 4
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Save Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue600,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save Settings")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsDialog(
    onDismiss: () -> Unit
) {
    var notificationSound by remember { mutableStateOf("Default") }
    var ledNotification by remember { mutableStateOf(true) }
    var quietHoursEnabled by remember { mutableStateOf(false) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Notification Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray800,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // Notification Sound
                Text(
                    text = "Notification Sound",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray700,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                var expanded by remember { mutableStateOf(false) }
                val sounds = listOf("Default", "Gentle", "Loud", "Silent")
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = notificationSound,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        sounds.forEach { sound ->
                            DropdownMenuItem(
                                text = { Text(sound) },
                                onClick = {
                                    notificationSound = sound
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // LED Notification
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LED Notification",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Gray700
                    )
                    Switch(
                        checked = ledNotification,
                        onCheckedChange = { ledNotification = it }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Quiet Hours
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quiet Hours",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Gray700
                    )
                    Switch(
                        checked = quietHoursEnabled,
                        onCheckedChange = { quietHoursEnabled = it }
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Save Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue600,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save Settings")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPreferencesDialog(
    onDismiss: () -> Unit
) {
    var theme by remember { mutableStateOf("System") }
    var timeFormat by remember { mutableStateOf("12 Hour") }
    var firstDayOfWeek by remember { mutableStateOf("Sunday") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "App Preferences",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray800,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // Theme
                Text(
                    text = "Theme",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray700,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                var themeExpanded by remember { mutableStateOf(false) }
                val themes = listOf("System", "Light", "Dark")
                
                ExposedDropdownMenuBox(
                    expanded = themeExpanded,
                    onExpandedChange = { themeExpanded = !themeExpanded }
                ) {
                    OutlinedTextField(
                        value = theme,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = themeExpanded,
                        onDismissRequest = { themeExpanded = false }
                    ) {
                        themes.forEach { themeOption ->
                            DropdownMenuItem(
                                text = { Text(themeOption) },
                                onClick = {
                                    theme = themeOption
                                    themeExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Time Format
                Text(
                    text = "Time Format",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray700,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                var timeExpanded by remember { mutableStateOf(false) }
                val timeFormats = listOf("12 Hour", "24 Hour")
                
                ExposedDropdownMenuBox(
                    expanded = timeExpanded,
                    onExpandedChange = { timeExpanded = !timeExpanded }
                ) {
                    OutlinedTextField(
                        value = timeFormat,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = timeExpanded,
                        onDismissRequest = { timeExpanded = false }
                    ) {
                        timeFormats.forEach { format ->
                            DropdownMenuItem(
                                text = { Text(format) },
                                onClick = {
                                    timeFormat = format
                                    timeExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // First Day of Week
                Text(
                    text = "First Day of Week",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray700,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                var weekExpanded by remember { mutableStateOf(false) }
                val weekDays = listOf("Sunday", "Monday")
                
                ExposedDropdownMenuBox(
                    expanded = weekExpanded,
                    onExpandedChange = { weekExpanded = !weekExpanded }
                ) {
                    OutlinedTextField(
                        value = firstDayOfWeek,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = weekExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = weekExpanded,
                        onDismissRequest = { weekExpanded = false }
                    ) {
                        weekDays.forEach { day ->
                            DropdownMenuItem(
                                text = { Text(day) },
                                onClick = {
                                    firstDayOfWeek = day
                                    weekExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Save Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue600,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save Settings")
                }
            }
        }
    }
}
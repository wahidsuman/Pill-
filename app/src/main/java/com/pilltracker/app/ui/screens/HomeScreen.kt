package com.pilltracker.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pilltracker.app.data.model.Pill
import com.pilltracker.app.ui.components.PillCard
import com.pilltracker.app.ui.components.AddPillModal
import com.pilltracker.app.ui.viewmodel.PillViewModel
import com.pilltracker.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PillViewModel
) {
    val pills by viewModel.pills.collectAsState()
    val showAddForm by viewModel.showAddForm.collectAsState()
    
    // Removed duplicate modal state - using only viewModel.showAddForm
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header (without add button)
        HomeHeader()
        
        // Stats boxes
        StatsBoxesSection(pills = pills)
        
        // Next reminders segment
        NextRemindersSection(pills = pills, viewModel = viewModel)
        
        // My medication segment
        MyMedicationSection(
            pills = pills,
            viewModel = viewModel,
            onAddPill = { 
                try {
                    viewModel.showAddForm()
                } catch (e: Exception) {
                    android.util.Log.e("HomeScreen", "Error showing add form: ${e.message}")
                }
            }
        )
    }
    
    // Add Pill Modal
    if (showAddForm) {
        AddPillModal(
            onDismiss = { 
                try {
                    viewModel.hideAddForm()
                } catch (e: Exception) {
                    android.util.Log.e("HomeScreen", "Error hiding add form: ${e.message}")
                }
            },
            onAddPill = { pill -> 
                try {
                    viewModel.addPill(pill)
                } catch (e: Exception) {
                    android.util.Log.e("HomeScreen", "Error adding pill: ${e.message}")
                }
            }
        )
    }
    
    // Removed duplicate AddPillModal - using only the one controlled by viewModel.showAddForm
}

@Composable
fun HomeHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Blue600),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Pill Reminder",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Never forget your Medicines",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
            Text(
                text = getCurrentDateTime(),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun StatsBoxesSection(pills: List<Pill>) {
    val takenToday = pills.count { it.taken }
    val totalToday = pills.size
    val pendingToday = totalToday - takenToday
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatBox(
            title = "Taken Today",
            value = takenToday.toString(),
            color = Green600,
            modifier = Modifier.weight(1f)
        )
        StatBox(
            title = "Pending",
            value = pendingToday.toString(),
            color = Orange600,
            modifier = Modifier.weight(1f)
        )
        StatBox(
            title = "Total",
            value = totalToday.toString(),
            color = Blue600,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Gray800),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
fun NextRemindersSection(
    pills: List<Pill>,
    viewModel: PillViewModel
) {
    val upcomingPills = pills.filter { !it.taken }.take(3)
    
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Next Reminders",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (pills.filter { !it.taken }.size > 3) {
                TextButton(onClick = { /* Navigate to see all reminders */ }) {
                    Text(
                        text = "See More",
                        color = Blue600,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        if (upcomingPills.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Gray800),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Gray400,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No upcoming reminders",
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(upcomingPills) { pill ->
                    SimpleReminderCard(pill = pill)
                }
            }
        }
    }
}

@Composable
fun SimpleReminderCard(pill: Pill) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Gray800),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = pill.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = pill.nextDose,
                fontSize = 14.sp,
                color = Blue600,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MyMedicationSection(
    pills: List<Pill>,
    viewModel: PillViewModel,
    onAddPill: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Medication",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            FloatingActionButton(
                onClick = onAddPill,
                modifier = Modifier.size(48.dp),
                containerColor = Blue600,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Medication",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (pills.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Gray800),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = null,
                        tint = Gray400,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No medications added yet",
                        fontSize = 16.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Add your first medication using the + button above",
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pills) { pill ->
                    PillCard(
                        pill = pill,
                        onMarkAsTaken = { viewModel.markAsTaken(pill) },
                        onEditPill = { viewModel.showEditForm(pill) },
                        onDeletePill = { viewModel.deletePill(pill) }
                    )
                }
            }
        }
    }
}




private fun getCurrentDateTime(): String {
    val calendar = java.util.Calendar.getInstance()
    val dateFormat = java.text.SimpleDateFormat("EEEE, MMMM dd, yyyy", java.util.Locale.getDefault())
    val timeFormat = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
    return "${dateFormat.format(calendar.time)} • ${timeFormat.format(calendar.time)}"
}

private fun getTimeOfDay(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Morning"
        in 12..17 -> "Afternoon"
        in 18..21 -> "Evening"
        else -> "Night"
    }
}
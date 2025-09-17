package com.pilltracker.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    viewModel: PillViewModel? = null
) {
    val pills by viewModel?.pills?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val showAddForm by viewModel?.showAddForm?.collectAsState() ?: remember { mutableStateOf(false) }
    
    var showAddPillModal by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header (without add button)
        HomeHeader()
        
        // Stats boxes
        StatsBoxesSection(pills = pills)
        
        // Next reminders segment
        NextRemindersSection(pills = pills, viewModel = viewModel)
        
        // My medication segment
        MyMedicationSection(
            onAddPill = { 
                viewModel?.showAddForm() ?: run { showAddPillModal = true }
            }
        )
        
        // All medications list
        AllMedicationsSection(pills = pills, viewModel = viewModel)
    }
    
    // Add Pill Modal
    if (showAddForm) {
        AddPillModal(
            onDismiss = { viewModel?.hideAddForm() },
            onAddPill = { pill -> viewModel?.addPill(pill) }
        )
    }
    
    if (showAddPillModal) {
        AddPillModal(
            onDismiss = { showAddPillModal = false },
            onAddPill = { /* Handle add pill */ }
        )
    }
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
            icon = Icons.Default.CheckCircle,
            color = Green600,
            modifier = Modifier.weight(1f)
        )
        StatBox(
            title = "Pending",
            value = pendingToday.toString(),
            icon = Icons.Default.Schedule,
            color = Orange600,
            modifier = Modifier.weight(1f)
        )
        StatBox(
            title = "Total",
            value = totalToday.toString(),
            icon = Icons.Default.Medication,
            color = Blue600,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatBox(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Gray800
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = Gray600,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NextRemindersSection(
    pills: List<Pill>,
    viewModel: PillViewModel? = null
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
                color = Gray800
            )
            if (pills.filter { !it.taken }.size > 3) {
                TextButton(onClick = { /* Navigate to see all reminders */ }) {
                    Text("See More")
                }
            }
        }
        
        if (upcomingPills.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Gray50),
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
                        color = Gray600,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(upcomingPills) { pill ->
                    PillCard(
                        pill = pill,
                        onMarkAsTaken = { viewModel?.markAsTaken(pill) },
                        onEditPill = { viewModel?.showEditForm(pill) },
                        onDeletePill = { viewModel?.deletePill(pill) }
                    )
                }
            }
        }
    }
}

@Composable
fun MyMedicationSection(
    onAddPill: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "My Medication",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Gray800,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Blue50),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FloatingActionButton(
                    onClick = onAddPill,
                    modifier = Modifier.size(64.dp),
                    containerColor = Blue600,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Medication",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Add New Medication",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Blue600,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Tap to add your first medication",
                    fontSize = 12.sp,
                    color = Gray600,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AllMedicationsSection(
    pills: List<Pill>,
    viewModel: PillViewModel? = null
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "All Medications",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Gray800,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        if (pills.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Gray50),
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
                        color = Gray600,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Add your first medication using the + button above",
                        fontSize = 14.sp,
                        color = Gray600,
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
                        onMarkAsTaken = { viewModel?.markAsTaken(pill) },
                        onEditPill = { viewModel?.showEditForm(pill) },
                        onDeletePill = { viewModel?.deletePill(pill) }
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
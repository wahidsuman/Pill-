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
        // Header with greeting and add button
        HomeHeader(
            onAddPill = { 
                viewModel?.showAddForm() ?: run { showAddPillModal = true }
            }
        )
        
        // Today's medications
        TodayMedicationsSection(pills = pills, viewModel = viewModel)
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
fun HomeHeader(onAddPill: () -> Unit) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
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
                
                FloatingActionButton(
                    onClick = onAddPill,
                    modifier = Modifier.size(56.dp),
                    containerColor = Color.White,
                    contentColor = Blue600
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Medication",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TodayMedicationsSection(
    pills: List<Pill>,
    viewModel: PillViewModel? = null
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Today's Medications",
            fontSize = 20.sp,
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
                        text = "Tap the + button to add your first medication",
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
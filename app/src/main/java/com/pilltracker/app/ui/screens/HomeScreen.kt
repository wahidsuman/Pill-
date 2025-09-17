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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pilltracker.app.data.model.Pill
import com.pilltracker.app.ui.components.PillCard
import com.pilltracker.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    // This will be populated with actual data from ViewModel
    val pills = remember { 
        listOf(
            Pill(
                id = 1,
                name = "Aspirin",
                dosage = "100mg",
                times = listOf("08:00", "20:00"),
                color = "blue",
                imagePath = "",
                nextDose = "08:00",
                frequency = "daily",
                customDays = emptyList(),
                taken = false
            ),
            Pill(
                id = 2,
                name = "Vitamin D",
                dosage = "1000 IU",
                times = listOf("12:00"),
                color = "orange",
                imagePath = "",
                nextDose = "12:00",
                frequency = "daily",
                customDays = emptyList(),
                taken = true
            ),
            Pill(
                id = 3,
                name = "Calcium",
                dosage = "500mg",
                times = listOf("18:00"),
                color = "green",
                imagePath = "",
                nextDose = "18:00",
                frequency = "daily",
                customDays = emptyList(),
                taken = false
            )
        )
    }
    
    var showAddPillModal by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header with greeting and add button
        HomeHeader(
            onAddPill = { showAddPillModal = true }
        )
        
        // Today's medications
        TodayMedicationsSection(pills = pills)
        
        // Recent medications
        RecentMedicationsSection(pills = pills)
    }
    
    // Add Pill Modal
    if (showAddPillModal) {
        // This would use the existing AddPillModal
        // AddPillModal(
        //     onDismiss = { showAddPillModal = false },
        //     onAddPill = { /* Handle add pill */ }
        // )
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
                        text = "Good ${getTimeOfDay()}!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Let's manage your medications",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f)
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
fun TodayMedicationsSection(pills: List<Pill>) {
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
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(pills) { pill ->
                PillCard(
                    pill = pill,
                    onMarkAsTaken = { /* Handle toggle */ },
                    onEditPill = { /* Handle edit */ },
                    onDeletePill = { /* Handle delete */ }
                )
            }
        }
    }
}

@Composable
fun RecentMedicationsSection(pills: List<Pill>) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Medications",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Gray800
            )
            
            TextButton(onClick = { /* Navigate to all medications */ }) {
                Text("View All")
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Quick stats cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStatCard(
                title = "Total Pills",
                value = pills.size.toString(),
                icon = Icons.Default.Medication,
                color = Blue600,
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                title = "Taken Today",
                value = pills.count { it.taken }.toString(),
                icon = Icons.Default.CheckCircle,
                color = Green600,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickStatCard(
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
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Gray800
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = Gray600
            )
        }
    }
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
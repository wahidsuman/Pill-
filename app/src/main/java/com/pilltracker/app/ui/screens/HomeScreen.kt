package com.pilltracker.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header Section
        HomeHeader()
        
        // Stats Boxes Section
        StatsBoxesSection()
        
        // Next Reminders Section
        NextRemindersSection()
        
        // My Medication Section
        MyMedicationSection()
    }
}


@Composable
fun HomeHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3B82F6)),
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
fun StatsBoxesSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatBox(
            title = "Taken Today",
            value = "0",
            color = Color(0xFF10B981),
            modifier = Modifier.weight(1f)
        )
        StatBox(
            title = "Pending",
            value = "0",
            color = Color(0xFFF59E0B),
            modifier = Modifier.weight(1f)
        )
        StatBox(
            title = "Total",
            value = "0",
            color = Color(0xFF3B82F6),
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
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
fun NextRemindersSection() {
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
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
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
                    tint = Color(0xFF9CA3AF),
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
    }
}


@Composable
fun MyMedicationSection() {
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
                onClick = { /* Add medication functionality removed */ },
                modifier = Modifier.size(48.dp),
                containerColor = Color(0xFF3B82F6),
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
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
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
                    tint = Color(0xFF9CA3AF),
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
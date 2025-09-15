package com.pilltracker.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pilltracker.app.data.model.Pill
import com.pilltracker.app.ui.components.*
import com.pilltracker.app.ui.theme.*
import com.pilltracker.app.ui.viewmodel.PillViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PillTrackerScreen(
    modifier: Modifier = Modifier,
    viewModel: PillViewModel = hiltViewModel()
) {
    val pills by viewModel.pills.collectAsState()
    val upcomingPills by viewModel.upcomingPills.collectAsState()
    val takenCount by viewModel.takenCount.collectAsState()
    val pendingCount by viewModel.pendingCount.collectAsState()
    val showAddForm by viewModel.showAddForm.collectAsState()
    val showEditForm by viewModel.showEditForm.collectAsState()
    var is24HourFormat by remember { mutableStateOf(false) }

    val currentTime = remember { LocalDateTime.now() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Blue50, Color.White),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(0.dp)
        ) {
            // Header
            item {
                HeaderSection(
                    currentTime = currentTime,
                    is24HourFormat = is24HourFormat,
                    onTimeFormatChange = { is24HourFormat = it }
                )
            }

            // Quick Stats
            item {
                QuickStatsSection(
                    takenCount = takenCount,
                    pendingCount = pendingCount,
                    totalCount = pills.size
                )
            }

            // Upcoming Reminders
            if (upcomingPills.isNotEmpty()) {
                item {
                    UpcomingRemindersSection(upcomingPills = upcomingPills)
                }
            }

            // Pills List
            item {
                PillsListSection(
                    pills = pills,
                    onAddPill = { viewModel.showAddForm() },
                    onMarkAsTaken = { viewModel.markAsTaken(it) },
                    onDeletePill = { viewModel.deletePill(it) },
                    onEditPill = { viewModel.showEditForm(it) }
                )
            }
        }

        // Add Pill Modal
        if (showAddForm) {
            AddPillModal(
                onDismiss = { viewModel.hideAddForm() },
                onAddPill = { viewModel.addPill(it) }
            )
        }

        // Edit Pill Modal
        if (showEditForm != null) {
            AddPillModal(
                onDismiss = { viewModel.hideEditForm() },
                onAddPill = { viewModel.updatePill(it) }
            )
        }
    }
}

@Composable
fun HeaderSection(
    currentTime: LocalDateTime,
    is24HourFormat: Boolean,
    onTimeFormatChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = null,
                            tint = Blue500,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "PillTracker",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gray800
                        )
                    }
                    Text(
                        text = currentTime.format(DateTimeFormatter.ofPattern("EEEE, MMM dd")),
                        fontSize = 14.sp,
                        color = Gray600,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = if (is24HourFormat) {
                            currentTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                        } else {
                            currentTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
                        },
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blue600
                    )
                    Text(
                        text = "Current Time",
                        fontSize = 12.sp,
                        color = Gray600
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Time Format Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Time Format",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray700
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "12h",
                        fontSize = 12.sp,
                        color = if (!is24HourFormat) Blue600 else Gray600
                    )
                    Switch(
                        checked = is24HourFormat,
                        onCheckedChange = onTimeFormatChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Blue600,
                            checkedTrackColor = Blue100,
                            uncheckedThumbColor = Gray600,
                            uncheckedTrackColor = Gray200
                        )
                    )
                    Text(
                        text = "24h",
                        fontSize = 12.sp,
                        color = if (is24HourFormat) Blue600 else Gray600
                    )
                }
            }
        }
    }
}

@Composable
fun QuickStatsSection(
    takenCount: Int,
    pendingCount: Int,
    totalCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Taken Today",
            value = takenCount.toString(),
            color = Green600,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Pending",
            value = pendingCount.toString(),
            color = Orange600,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Total",
            value = totalCount.toString(),
            color = Blue600,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = Gray600,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 1
            )
        }
    }
}

@Composable
fun UpcomingRemindersSection(upcomingPills: List<Pill>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = Orange500,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Next Reminders",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray800
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        upcomingPills.take(2).forEach { pill ->
            UpcomingReminderCard(pill = pill)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun UpcomingReminderCard(pill: Pill) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PillIcon(color = pill.color, size = 32.dp)
                Column {
                    Text(
                        text = pill.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9A3412)
                    )
                    Text(
                        text = "${pill.nextDose} • ${pill.dosage}",
                        fontSize = 14.sp,
                        color = Color(0xFFC2410C)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = Orange500,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun PillsListSection(
    pills: List<Pill>,
    onAddPill: () -> Unit,
    onMarkAsTaken: (Pill) -> Unit,
    onDeletePill: (Pill) -> Unit,
    onEditPill: (Pill) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Medications",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray800
            )
            FloatingActionButton(
                onClick = onAddPill,
                containerColor = Blue600,
                contentColor = Color.White,
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, RoundedCornerShape(28.dp)),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Pill",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        pills.forEach { pill ->
            PillCard(
                pill = pill,
                onMarkAsTaken = { onMarkAsTaken(pill) },
                onDeletePill = { onDeletePill(pill) },
                onEditPill = { onEditPill(pill) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
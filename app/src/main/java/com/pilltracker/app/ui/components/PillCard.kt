package com.pilltracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pilltracker.app.data.model.Pill
import com.pilltracker.app.ui.theme.*

@Composable
fun PillCard(
    pill: Pill,
    onMarkAsTaken: () -> Unit,
    onDeletePill: () -> Unit,
    onEditPill: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pill Icon with taken indicator
            Box {
                PillIcon(
                    color = pill.color,
                    size = 48.dp,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                )
                if (pill.taken) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                color = Green500,
                                shape = CircleShape
                            )
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(12.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Pill Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pill.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (pill.taken) Green600 else Gray800
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Take/Taken Button
                        Button(
                            onClick = onMarkAsTaken,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (pill.taken) Color(0xFFDCFCE7) else Color(0xFFDBEAFE),
                                contentColor = if (pill.taken) Green700 else Blue700
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = if (pill.taken) "Taken" else "Take",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Edit Button
                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Blue600,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        // Delete Button
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Gray600,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = "${pill.dosage} • Next: ${pill.nextDose}",
                    fontSize = 14.sp,
                    color = Gray600,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                // Frequency info
                val frequencyText = when (pill.frequency) {
                    "daily" -> "Daily"
                    "weekly" -> "Weekly"
                    "monthly" -> "Monthly"
                    "custom" -> if (pill.customDays.isNotEmpty()) {
                        "Custom (${pill.customDays.joinToString(", ") { it.take(3) }})"
                    } else "Custom"
                    else -> "Daily"
                }
                
                Text(
                    text = frequencyText,
                    fontSize = 12.sp,
                    color = Blue600,
                    modifier = Modifier.padding(top = 2.dp)
                )
                
                // Time slots
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    pill.times.forEach { time ->
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Gray100,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = time,
                                fontSize = 12.sp,
                                color = Gray600
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Medication",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Are you sure you want to delete ${pill.name}? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeletePill()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Red500,
                        contentColor = Color.White
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Edit Confirmation Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = "Edit Medication",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Are you sure you want to edit ${pill.name}? This will open the edit form.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEditPill()
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue600,
                        contentColor = Color.White
                    )
                ) {
                    Text("Edit")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showEditDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
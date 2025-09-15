package com.pilltracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pilltracker.app.data.model.Pill
import com.pilltracker.app.ui.theme.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.*

@Composable
fun AddPillModal(
    onDismiss: () -> Unit,
    onAddPill: (Pill) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("blue") }
    var times by remember { mutableStateOf(listOf("")) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedTimeIndex by remember { mutableStateOf(0) }

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
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Add New Medication",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray800
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Medication Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medication Name") },
                    placeholder = { Text("e.g., Aspirin") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dosage
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage") },
                    placeholder = { Text("e.g., 100mg") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pill Color
                Text(
                    text = "Pill Color",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray700,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val colors = listOf("blue", "red", "green", "orange", "purple")
                    colors.forEach { colorOption ->
                        ColorOption(
                            color = colorOption,
                            isSelected = color == colorOption,
                            onClick = { color = colorOption }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reminder Times
                Text(
                    text = "Reminder Times",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray700,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                times.forEachIndexed { index, time ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Time Picker Button
                        OutlinedButton(
                            onClick = {
                                selectedTimeIndex = index
                                showTimePicker = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Blue600
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (time.isBlank()) "Select Time" else time,
                                    color = if (time.isBlank()) Gray600 else Color.Black
                                )
                            }
                        }
                        
                        if (times.size > 1) {
                            IconButton(
                                onClick = {
                                    times = times.filterIndexed { i, _ -> i != index }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove time",
                                    tint = Red500
                                )
                            }
                        }
                    }
                    
                    if (index < times.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                TextButton(
                    onClick = {
                        times = times + ""
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add another time")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            if (name.isNotBlank() && dosage.isNotBlank()) {
                                val validTimes = times.filter { it.isNotBlank() }
                                if (validTimes.isNotEmpty()) {
                                    val pill = Pill(
                                        name = name,
                                        dosage = dosage,
                                        times = validTimes,
                                        color = color,
                                        nextDose = validTimes.first()
                                    )
                                    onAddPill(pill)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank() && dosage.isNotBlank() && times.any { it.isNotBlank() }
                    ) {
                        Text("Add Pill")
                    }
                }
            }
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onTimeSelected = { selectedTime ->
                val newTimes = times.toMutableList()
                newTimes[selectedTimeIndex] = selectedTime
                times = newTimes
                showTimePicker = false
            }
        )
    }
}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    var selectedHour by remember { mutableStateOf(8) }
    var selectedMinute by remember { mutableStateOf(0) }
    var isSelectingHour by remember { mutableStateOf(true) }

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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Time",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray800,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Time Display
                Text(
                    text = String.format("%02d:%02d", selectedHour, selectedMinute),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blue600,
                    modifier = Modifier.padding(16.dp)
                )

                // Mode Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { isSelectingHour = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelectingHour) Blue500 else Gray200,
                            contentColor = if (isSelectingHour) Color.White else Gray600
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Hour")
                    }
                    
                    Button(
                        onClick = { isSelectingHour = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isSelectingHour) Blue500 else Gray200,
                            contentColor = if (!isSelectingHour) Color.White else Gray600
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Minute")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Round Clock
                RoundClockPicker(
                    selectedValue = if (isSelectingHour) selectedHour else selectedMinute,
                    maxValue = if (isSelectingHour) 23 else 59,
                    onValueSelected = { value ->
                        if (isSelectingHour) {
                            selectedHour = value
                        } else {
                            selectedMinute = value
                        }
                    },
                    modifier = Modifier.size(280.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
                            onTimeSelected(timeString)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Set Time")
                    }
                }
            }
        }
    }
}

@Composable
fun RoundClockPicker(
    selectedValue: Int,
    maxValue: Int,
    onValueSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White)
            .pointerInput(Unit) {
                detectDragGestures { change ->
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val x = change.position.x - centerX
                    val y = change.position.y - centerY
                    
                    val angle = atan2(y, x) * 180 / PI
                    val normalizedAngle = (angle + 90 + 360) % 360
                    
                    val value = if (maxValue == 23) {
                        // For hours: 0-23
                        ((normalizedAngle / 360) * 24).toInt().coerceIn(0, 23)
                    } else {
                        // For minutes: 0-59
                        ((normalizedAngle / 360) * 60).toInt().coerceIn(0, 59)
                    }
                    
                    onValueSelected(value)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Clock face
        Box(
            modifier = Modifier
                .size(260.dp)
                .clip(CircleShape)
                .background(Blue50)
        )
        
        // Hour markers
        for (i in 0 until 12) {
            val angle = (i * 30 - 90) * PI / 180
            val radius = 100.dp
            val x = (cos(angle) * radius.value).dp
            val y = (sin(angle) * radius.value).dp
            
            Box(
                modifier = Modifier
                    .offset(x, y)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (i % 3 == 0) Blue600 else Blue300)
            )
        }
        
        // Selected value indicator
        val angle = if (maxValue == 23) {
            (selectedValue * 15 - 90) * PI / 180 // 24 hours = 360 degrees
        } else {
            (selectedValue * 6 - 90) * PI / 180 // 60 minutes = 360 degrees
        }
        val radius = 80.dp
        val x = (cos(angle) * radius.value).dp
        val y = (sin(angle) * radius.value).dp
        
        Box(
            modifier = Modifier
                .offset(x, y)
                .size(20.dp)
                .clip(CircleShape)
                .background(Blue600)
        )
        
        // Center dot
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(Blue600)
        )
    }
}

@Composable
fun ColorOption(
    color: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorValue = when (color.lowercase()) {
        "blue" -> Blue500
        "red" -> Red500
        "green" -> Green500
        "orange" -> Orange500
        "purple" -> Purple500
        else -> Blue500
    }

    Card(
        modifier = Modifier
            .size(40.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = colorValue),
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(3.dp, Color.White)
        } else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
package com.pilltracker.app.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pilltracker.app.data.model.Pill
import com.pilltracker.app.ui.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPillModal(
    onDismiss: () -> Unit,
    onAddPill: (Pill) -> Unit,
    editPill: Pill? = null
) {
    var name by remember { mutableStateOf(editPill?.name ?: "") }
    var dosage by remember { mutableStateOf(editPill?.dosage ?: "") }
    var color by remember { mutableStateOf(editPill?.color ?: "blue") }
    var times by remember { mutableStateOf(editPill?.times ?: listOf("")) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedTimeIndex by remember { mutableStateOf(0) }
    var frequency by remember { mutableStateOf(editPill?.frequency ?: "daily") }
    var customDays by remember { mutableStateOf(editPill?.customDays ?: listOf<String>()) }
    var showCustomDaysPicker by remember { mutableStateOf(false) }

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
                    text = if (editPill != null) "Edit Medication" else "Add New Medication",
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

                // Frequency Selection
                Text(
                    text = "Frequency",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray700,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val frequencies = listOf(
                        "daily" to "Daily",
                        "weekly" to "Weekly", 
                        "monthly" to "Monthly",
                        "custom" to "Custom"
                    )
                    frequencies.forEach { (freq, displayText) ->
                        FilterChip(
                            onClick = { 
                                frequency = freq
                                if (freq == "custom") {
                                    showCustomDaysPicker = true
                                }
                            },
                            label = { 
                                Text(
                                    text = displayText,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            },
                            selected = frequency == freq,
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Blue100,
                                selectedLabelColor = Blue600,
                                containerColor = Gray100,
                                labelColor = Gray700
                            )
                        )
                    }
                }

                // Show custom days if custom frequency is selected
                if (frequency == "custom" && customDays.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Selected Days: ${customDays.joinToString(", ")}",
                        fontSize = 12.sp,
                        color = Blue600,
                        modifier = Modifier.padding(start = 4.dp)
                    )
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
                                        id = editPill?.id ?: 0,
                                        name = name,
                                        dosage = dosage,
                                        times = validTimes,
                                        color = color,
                                        nextDose = validTimes.first(),
                                        frequency = frequency,
                                        customDays = if (frequency == "custom") customDays else emptyList(),
                                        taken = editPill?.taken ?: false
                                    )
                                    onAddPill(pill)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank() && dosage.isNotBlank() && times.any { it.isNotBlank() } && 
                                (frequency != "custom" || customDays.isNotEmpty()),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Blue600,
                            contentColor = Color.White,
                            disabledContainerColor = Gray200,
                            disabledContentColor = Gray600
                        )
                    ) {
                        Text(
                            if (editPill != null) "Update Pill" else "Add Pill",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // Native Time Picker Dialog
    if (showTimePicker) {
        NativeTimePickerDialog(
            onDismiss = { showTimePicker = false },
            onTimeSelected = { selectedTime ->
                val newTimes = times.toMutableList()
                newTimes[selectedTimeIndex] = selectedTime
                times = newTimes
                showTimePicker = false
            }
        )
    }

    // Custom Days Picker Dialog
    if (showCustomDaysPicker) {
        CustomDaysPickerDialog(
            onDismiss = { showCustomDaysPicker = false },
            onDaysSelected = { selectedDays ->
                customDays = selectedDays
                showCustomDaysPicker = false
            },
            selectedDays = customDays
        )
    }
}

@Composable
fun NativeTimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val calendar = remember { Calendar.getInstance() }
    var selectedHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }
    var isAM by remember { mutableStateOf(selectedHour < 12) }
    
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray800
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Native-style time display with AM/PM differentiation
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Hour
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Hour",
                            fontSize = 12.sp,
                            color = Gray600,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    val displayHour = if (selectedHour == 0) 12 else if (selectedHour > 12) selectedHour - 12 else selectedHour
                                    if (displayHour > 1) {
                                        val newHour = if (displayHour == 12) 1 else displayHour - 1
                                        selectedHour = if (isAM) newHour else newHour + 12
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Increase hour",
                                    tint = Blue600
                                )
                            }
                            
                            Text(
                                text = run {
                                    val displayHour = if (selectedHour == 0) 12 else if (selectedHour > 12) selectedHour - 12 else selectedHour
                                    String.format("%02d", displayHour)
                                },
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Gray800,
                                modifier = Modifier.width(48.dp),
                                textAlign = TextAlign.Center
                            )
                            
                            IconButton(
                                onClick = {
                                    val displayHour = if (selectedHour == 0) 12 else if (selectedHour > 12) selectedHour - 12 else selectedHour
                                    if (displayHour < 12) {
                                        val newHour = if (displayHour == 11) 12 else displayHour + 1
                                        selectedHour = if (isAM) newHour else newHour + 12
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Decrease hour",
                                    tint = Blue600
                                )
                            }
                        }
                    }

                    Text(
                        text = ":",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray800
                    )

                    // Minute
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Minute",
                            fontSize = 12.sp,
                            color = Gray600,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    selectedMinute = (selectedMinute - 1 + 60) % 60
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Increase minute",
                                    tint = Blue600
                                )
                            }
                            
                            Text(
                                text = String.format("%02d", selectedMinute),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Gray800,
                                modifier = Modifier.width(48.dp),
                                textAlign = TextAlign.Center
                            )
                            
                            IconButton(
                                onClick = {
                                    selectedMinute = (selectedMinute + 1) % 60
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Decrease minute",
                                    tint = Blue600
                                )
                            }
                        }
                    }
                }

                // AM/PM Selection with clear differentiation
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "AM/PM",
                    fontSize = 12.sp,
                    color = Gray600,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            isAM = true
                            if (selectedHour >= 12) {
                                selectedHour = selectedHour - 12
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAM) Blue600 else Gray200,
                            contentColor = if (isAM) Color.White else Gray700
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "AM",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Button(
                        onClick = {
                            isAM = false
                            if (selectedHour < 12) {
                                selectedHour = selectedHour + 12
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isAM) Blue600 else Gray200,
                            contentColor = if (!isAM) Color.White else Gray700
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "PM",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
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
                            val hour = if (selectedHour == 0) 12 else if (selectedHour > 12) selectedHour - 12 else selectedHour
                            val amPm = if (isAM) "AM" else "PM"
                            val timeString = String.format("%02d:%02d %s", hour, selectedMinute, amPm)
                            onTimeSelected(timeString)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Blue600,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "OK",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomDaysPickerDialog(
    onDismiss: () -> Unit,
    onDaysSelected: (List<String>) -> Unit,
    selectedDays: List<String>
) {
    var tempSelectedDays by remember { mutableStateOf(selectedDays.toSet()) }
    
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
                    text = "Select Days",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray800
                )

                Spacer(modifier = Modifier.height(16.dp))

                val daysOfWeek = listOf(
                    "Monday" to "Mon",
                    "Tuesday" to "Tue", 
                    "Wednesday" to "Wed",
                    "Thursday" to "Thu",
                    "Friday" to "Fri",
                    "Saturday" to "Sat",
                    "Sunday" to "Sun"
                )

                daysOfWeek.forEach { (fullDay, shortDay) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                tempSelectedDays = if (tempSelectedDays.contains(fullDay)) {
                                    tempSelectedDays - fullDay
                                } else {
                                    tempSelectedDays + fullDay
                                }
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = fullDay,
                            fontSize = 16.sp,
                            color = Gray800
                        )
                        
                        Checkbox(
                            checked = tempSelectedDays.contains(fullDay),
                            onCheckedChange = { isChecked ->
                                tempSelectedDays = if (isChecked) {
                                    tempSelectedDays + fullDay
                                } else {
                                    tempSelectedDays - fullDay
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Blue600,
                                uncheckedColor = Gray400
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                            onDaysSelected(tempSelectedDays.toList())
                        },
                        modifier = Modifier.weight(1f),
                        enabled = tempSelectedDays.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Blue600,
                            contentColor = Color.White,
                            disabledContainerColor = Gray200,
                            disabledContentColor = Gray600
                        )
                    ) {
                        Text(
                            "Done",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
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
            BorderStroke(3.dp, Color.White)
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
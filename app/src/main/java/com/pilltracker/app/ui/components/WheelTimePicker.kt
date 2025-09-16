package com.pilltracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch

@Composable
fun WheelTimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit,
    currentTime: String = ""
) {
    // Parse current time if available
    var selectedHour by remember { 
        mutableStateOf(
            if (currentTime.isNotBlank()) {
                try {
                    val timeParts = currentTime.split(":")
                    val hour = timeParts[0].toInt()
                    if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                } catch (e: Exception) {
                    12
                }
            } else {
                12
            }
        )
    }
    
    var selectedMinute by remember { 
        mutableStateOf(
            if (currentTime.isNotBlank()) {
                try {
                    val timeParts = currentTime.split(":")
                    timeParts[1].toInt()
                } catch (e: Exception) {
                    0
                }
            } else {
                0
            }
        )
    }
    
    var selectedAmPm by remember { 
        mutableStateOf(
            if (currentTime.isNotBlank()) {
                try {
                    val timeParts = currentTime.split(":")
                    val hour = timeParts[0].toInt()
                    if (hour < 12) "AM" else "PM"
                } catch (e: Exception) {
                    "AM"
                }
            } else {
                "AM"
            }
        )
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color(0xFF00D4AA),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Text(
                        text = "New alarm",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    TextButton(
                        onClick = {
                            val hour24 = if (selectedAmPm == "AM") {
                                if (selectedHour == 12) 0 else selectedHour
                            } else {
                                if (selectedHour == 12) 12 else selectedHour + 12
                            }
                            val timeString = String.format("%02d:%02d", hour24, selectedMinute)
                            onTimeSelected(timeString)
                        }
                    ) {
                        Text(
                            text = "Done",
                            color = Color(0xFF00D4AA),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Time until alarm text
                Text(
                    text = "Ring in less than 1 minute",
                    color = Color(0xFF999999),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // Wheel Time Picker
                WheelTimePicker(
                    selectedHour = selectedHour,
                    selectedMinute = selectedMinute,
                    selectedAmPm = selectedAmPm,
                    onHourSelected = { selectedHour = it },
                    onMinuteSelected = { selectedMinute = it },
                    onAmPmSelected = { selectedAmPm = it }
                )
            }
        }
    }
}

@Composable
fun WheelTimePicker(
    selectedHour: Int,
    selectedMinute: Int,
    selectedAmPm: String,
    onHourSelected: (Int) -> Unit,
    onMinuteSelected: (Int) -> Unit,
    onAmPmSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hour Column
            WheelColumn(
                items = (1..12).toList(),
                selectedItem = selectedHour,
                onItemSelected = { onHourSelected(it as Int) },
                modifier = Modifier.weight(1f)
            )
            
            // Minute Column
            WheelColumn(
                items = (0..59).toList(),
                selectedItem = selectedMinute,
                onItemSelected = { onMinuteSelected(it as Int) },
                modifier = Modifier.weight(1f)
            )
            
            // AM/PM Column
            WheelColumn(
                items = listOf("AM", "PM"),
                selectedItem = selectedAmPm,
                onItemSelected = { onAmPmSelected(it as String) },
                modifier = Modifier.weight(0.7f)
            )
        }
    }
}

@Composable
fun WheelColumn(
    items: List<Any>,
    selectedItem: Any,
    onItemSelected: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var centerItem by remember { mutableStateOf(selectedItem) }
    
    // Calculate initial position and auto-scroll to selected item
    LaunchedEffect(selectedItem) {
        val index = items.indexOf(selectedItem)
        if (index >= 0) {
            listState.animateScrollToItem(index)
            centerItem = selectedItem
        }
    }
    
    // Update center item when scrolling - use derived state for better performance
    val currentCenterItem by remember {
        derivedStateOf {
            val centerIndex = listState.firstVisibleItemIndex + 2 // +2 for padding
            if (centerIndex < items.size) items[centerIndex] else centerItem
        }
    }
    
    // Update centerItem when derived state changes
    LaunchedEffect(currentCenterItem) {
        centerItem = currentCenterItem
    }
    
    Box(
        modifier = modifier.height(188.dp)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        coroutineScope.launch {
                            val currentIndex = listState.firstVisibleItemIndex
                            val newIndex = (currentIndex - (dragAmount.y / 48).toInt()).coerceIn(0, items.size - 1)
                            listState.animateScrollToItem(newIndex)
                            // Update center item and notify parent
                            val newCenterIndex = newIndex + 2 // +2 for padding
                            if (newCenterIndex < items.size) {
                                centerItem = items[newCenterIndex]
                                onItemSelected(items[newCenterIndex])
                            }
                        }
                    }
                },
            verticalArrangement = Arrangement.spacedBy(0.dp),
            contentPadding = PaddingValues(vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(items.size) { index ->
                val item = items[index]
                val isInCenter = item == centerItem
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clickable { 
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                                // Update center item after scrolling
                                centerItem = item
                            }
                            onItemSelected(item) 
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (item) {
                            is Int -> if (item < 10) "0$item" else item.toString()
                            else -> item.toString()
                        },
                        color = if (isInCenter) Color(0xFF1976D2) else Color(0xFF999999), // Deep blue for center, grey for others
                        fontSize = if (isInCenter) 24.sp else 18.sp,
                        fontWeight = if (isInCenter) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        // Selection indicator lines with better visibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 60.dp)
        ) {
            // Top line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .offset(y = 24.dp)
                    .background(Color(0xFF444444))
            )
            
            // Bottom line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .offset(y = 72.dp)
                    .background(Color(0xFF444444))
            )
            
            // Center selection highlight - more prominent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .offset(y = 24.dp)
                    .background(
                        Color(0xFF1976D2).copy(alpha = 0.15f),
                        RoundedCornerShape(6.dp)
                    )
            )
        }
    }
}
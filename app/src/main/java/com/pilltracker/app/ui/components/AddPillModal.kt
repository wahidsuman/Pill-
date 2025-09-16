package com.pilltracker.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.net.Uri
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pilltracker.app.data.model.Pill
import com.pilltracker.app.ui.theme.*
import java.util.*
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent

// Extension function to convert Color to ARGB
private fun Color.toArgb(): Int {
    return (alpha * 255).toInt() shl 24 or
           (red * 255).toInt() shl 16 or
           (green * 255).toInt() shl 8 or
           (blue * 255).toInt()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPillModal(
    onDismiss: () -> Unit,
    onAddPill: (Pill) -> Unit,
    editPill: Pill? = null
) {
    var name by remember { mutableStateOf(editPill?.name ?: "") }
    var color by remember { mutableStateOf(editPill?.color ?: "blue") }
    var imagePath by remember { mutableStateOf(editPill?.imagePath ?: "") }
    var useImage by remember { mutableStateOf(editPill?.imagePath?.isNotEmpty() == true) }
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

                // Image or Color Selection
                Text(
                    text = "Pill Representation",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray700,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Toggle between Image and Color
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = { 
                            useImage = true
                            color = "blue" // Reset color when switching to image
                        },
                        label = { 
                            Text(
                                text = "Take Photo",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        selected = useImage,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Blue100,
                            selectedLabelColor = Blue600,
                            containerColor = Gray100,
                            labelColor = Gray700
                        )
                    )
                    
                    FilterChip(
                        onClick = { 
                            useImage = false
                            imagePath = "" // Clear image when switching to color
                        },
                        label = { 
                            Text(
                                text = "Use Color",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        selected = !useImage,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Blue100,
                            selectedLabelColor = Blue600,
                            containerColor = Gray100,
                            labelColor = Gray700
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Show Image Capture or Color Selection based on toggle
                if (useImage) {
                    // Image Capture Section
                    ImageCaptureSection(
                        imagePath = imagePath,
                        onImageCaptured = { path -> imagePath = path }
                    )
                } else {
                    // Color Selection Section
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

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    val frequencies = listOf(
                        "daily" to "Daily",
                        "weekly" to "Weekly", 
                        "monthly" to "Monthly",
                        "custom" to "Custom"
                    )
                    
                    items(frequencies.size) { index ->
                        val (freq, displayText) = frequencies[index]
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
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                            },
                            selected = frequency == freq,
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
                                if (time.isBlank()) {
                                    Text(
                                        text = "Select Time",
                                        color = Gray600
                                    )
                                } else {
                                    // Parse time to show AM/PM with visual distinction
                                    val timeParts = time.split(" ")
                                    val timeOnly = timeParts[0]
                                    val ampm = if (timeParts.size > 1) timeParts[1] else ""
                                    
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = timeOnly,
                                            color = Color.Black,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = ampm,
                                            color = Color.Black,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
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
                            if (name.isNotBlank()) {
                                val validTimes = times.filter { it.isNotBlank() }
                                if (validTimes.isNotEmpty()) {
                                    val pill = Pill(
                                        id = editPill?.id ?: 0,
                                        name = name,
                                        dosage = "", // No longer used
                                        times = validTimes,
                                        color = if (useImage) "" else color,
                                        imagePath = if (useImage) imagePath else "",
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
                        enabled = name.isNotBlank() && times.any { it.isNotBlank() } && 
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


    // Native Time Picker Dialog with custom AM/PM selector
    if (showTimePicker) {
        val context = LocalContext.current
        
        // Parse current time if available
        val currentTime = times[selectedTimeIndex]
        var hour: Int
        var minute: Int
        
        if (currentTime.isNotBlank() && currentTime != "Select Time") {
            try {
                val timeParts = currentTime.split(" ")
                val timeOnly = timeParts[0].split(":")
                val ampm = timeParts[1]
                
                hour = if (ampm == "AM") {
                    if (timeOnly[0].toInt() == 12) 0 else timeOnly[0].toInt()
                } else {
                    if (timeOnly[0].toInt() == 12) 12 else timeOnly[0].toInt() + 12
                }
                minute = timeOnly[1].toInt()
            } catch (e: Exception) {
                hour = 12
                minute = 0
            }
        } else {
            hour = 12
            minute = 0
        }
        
        // Use LaunchedEffect to show the time picker with selectedTimeIndex as key
        LaunchedEffect(showTimePicker, selectedTimeIndex) {
            if (showTimePicker) {
                try {
                    // Create a custom styled TimePickerDialog
                    val timePickerDialog = object : TimePickerDialog(
                        context,
                        { _, selectedHour, selectedMinute ->
                            val displayHour = if (selectedHour == 0) 12 else if (selectedHour > 12) selectedHour - 12 else selectedHour
                            val ampm = if (selectedHour < 12) "AM" else "PM"
                            val timeString = String.format("%02d:%02d %s", displayHour, selectedMinute, ampm)
                            
                            val newTimes = times.toMutableList()
                            newTimes[selectedTimeIndex] = timeString
                            times = newTimes
                            showTimePicker = false
                        },
                        hour,
                        minute,
                        false // 12-hour format
                    ) {
                        override fun onTimeChanged(view: android.widget.TimePicker?, hourOfDay: Int, minute: Int) {
                            super.onTimeChanged(view, hourOfDay, minute)
                            
                            // Apply custom styling to AM/PM buttons
                            try {
                                val timePicker = view ?: return
                                
                                // Try multiple approaches to access AM/PM button
                                
                                // Method 1: Try to access mAmPmButton field
                                try {
                                    val amPmButtonField = timePicker.javaClass.getDeclaredField("mAmPmButton")
                                    amPmButtonField.isAccessible = true
                                    val amPmButton = amPmButtonField.get(timePicker) as? android.widget.Button
                                    
                                    amPmButton?.let { button ->
                                        button.setBackgroundColor(Gray200.toArgb())
                                        button.setTextColor(Color.Black.toArgb())
                                        button.textSize = 16f
                                        button.typeface = android.graphics.Typeface.DEFAULT_BOLD
                                    }
                                } catch (e: Exception) {
                                    // Method 2: Try to find AM/PM button by text
                                    try {
                                        val parent = timePicker.parent as? android.view.ViewGroup
                                        parent?.let { viewGroup ->
                                            for (i in 0 until viewGroup.childCount) {
                                                val child = viewGroup.getChildAt(i)
                                                if (child is android.widget.Button) {
                                                    val buttonText = child.text.toString()
                                                    if (buttonText == "AM" || buttonText == "PM") {
                                                        child.setBackgroundColor(Gray200.toArgb())
                                                        child.setTextColor(Color.Black.toArgb())
                                                        child.textSize = 16f
                                                        child.typeface = android.graphics.Typeface.DEFAULT_BOLD
                                                    }
                                                }
                                            }
                                        }
                                    } catch (e2: Exception) {
                                        // If all methods fail, just continue
                                        e2.printStackTrace()
                                    }
                                }
                            } catch (e: Exception) {
                                // Fallback if reflection fails
                                e.printStackTrace()
                            }
                        }
                    }
                    
                    timePickerDialog.setTitle("Select Medication Time")
                    timePickerDialog.show()
                } catch (e: Exception) {
                    // Fallback: just close the picker
                    showTimePicker = false
                }
            }
        }
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

                daysOfWeek.forEach { (fullDay, _) ->
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

@Composable
fun ImageCaptureSection(
    imagePath: String,
    onImageCaptured: (String) -> Unit
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    
    // Check camera permission
    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }
    
    // Create a temporary file for the image
    val imageFile = remember {
        File(context.filesDir, "pill_images").apply {
            if (!exists()) mkdirs()
        }
    }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            // Image was captured successfully
            val filePath = imageUri!!.path ?: ""
            if (filePath.isNotEmpty() && File(filePath).exists()) {
                onImageCaptured(filePath)
            }
        }
    }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                // Copy the selected image to our private storage
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val photoFile = File(imageFile, "pill_gallery_${timestamp}.jpg")
                
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    photoFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                onImageCaptured(photoFile.absolutePath)
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (imagePath.isNotEmpty()) {
            // Show captured image
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .border(
                        2.dp,
                        Blue600,
                        RoundedCornerShape(8.dp)
                    ),
                shape = RoundedCornerShape(8.dp)
            ) {
                val bitmap = remember(imagePath) {
                    try {
                        BitmapFactory.decodeFile(imagePath)
                    } catch (e: Exception) {
                        null
                    }
                }
                
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Captured pill image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Show placeholder if image can't be loaded
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Image placeholder",
                            modifier = Modifier.size(48.dp),
                            tint = Gray400
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Remove image button
            TextButton(
                onClick = { onImageCaptured("") }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove image",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Remove Image")
            }
        } else {
            // Show camera and gallery options
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Camera button
                OutlinedButton(
                    onClick = {
                        if (!hasCameraPermission) {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        } else {
                            try {
                                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                val photoFile = File(imageFile, "pill_${timestamp}.jpg")
                                
                                // Create the file
                                photoFile.createNewFile()
                                
                                imageUri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    photoFile
                                )
                                
                                cameraLauncher.launch(imageUri)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Blue600
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Take photo",
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Camera",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Gallery button
                OutlinedButton(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Blue600
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Select from gallery",
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Gallery",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomTimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit,
    currentTime: String
) {
    // Parse current time if available
    var selectedHour by remember { 
        mutableStateOf(
            if (currentTime.isNotBlank() && currentTime != "Select Time") {
                try {
                    val timeParts = currentTime.split(" ")
                    val timeOnly = timeParts[0].split(":")
                    val ampm = timeParts[1]
                    if (ampm == "AM") {
                        if (timeOnly[0].toInt() == 12) 0 else timeOnly[0].toInt()
                    } else {
                        if (timeOnly[0].toInt() == 12) 12 else timeOnly[0].toInt() + 12
                    }
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
            if (currentTime.isNotBlank() && currentTime != "Select Time") {
                try {
                    val timeParts = currentTime.split(" ")
                    val timeOnly = timeParts[0].split(":")
                    timeOnly[1].toInt()
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
            if (currentTime.isNotBlank() && currentTime != "Select Time") {
                try {
                    val timeParts = currentTime.split(" ")
                    timeParts[1]
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
                Text(
                    text = "Select Medication Time",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // Time Display and AM/PM Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Digital Time Display
                    Text(
                        text = String.format("%02d:%02d", 
                            if (selectedHour == 0) 12 else if (selectedHour > 12) selectedHour - 12 else selectedHour,
                            selectedMinute
                        ),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    
                    // Scrollable AM/PM Selector
                    ScrollableAmPmSelector(
                        selectedAmPm = selectedAmPm,
                        onAmPmSelected = { selectedAmPm = it }
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Analog Clock Face
                AnalogClockFace(
                    selectedHour = selectedHour,
                    selectedMinute = selectedMinute,
                    onHourSelected = { selectedHour = it }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Control Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Keyboard icon (placeholder for now)
                    IconButton(
                        onClick = { /* TODO: Implement keyboard input */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = "Keyboard input",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    // Cancel and OK buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TextButton(
                            onClick = onDismiss
                        ) {
                            Text(
                                text = "CANCEL",
                                color = Color(0xFF00FF00),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        TextButton(
                            onClick = {
                                val displayHour = if (selectedHour == 0) 12 else if (selectedHour > 12) selectedHour - 12 else selectedHour
                                val timeString = String.format("%02d:%02d %s", displayHour, selectedMinute, selectedAmPm)
                                onTimeSelected(timeString)
                            }
                        ) {
                            Text(
                                text = "OK",
                                color = Color(0xFF00FF00),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScrollableAmPmSelector(
    selectedAmPm: String,
    onAmPmSelected: (String) -> Unit
) {
    val listState = rememberLazyListState()
    
    // Calculate the initial position based on selected AM/PM
    val initialIndex = if (selectedAmPm == "AM") 1 else 2
    
    LaunchedEffect(selectedAmPm) {
        listState.animateScrollToItem(initialIndex)
    }
    
    Box(
        modifier = Modifier
            .height(120.dp)
            .width(60.dp)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            contentPadding = PaddingValues(vertical = 40.dp)
        ) {
            // Empty space at top
            item {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "",
                        color = Color.Transparent,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // AM option
            item {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .clickable { onAmPmSelected("AM") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AM",
                        color = if (selectedAmPm == "AM") Color.White else Color(0xFF666666),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // PM option
            item {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .clickable { onAmPmSelected("PM") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PM",
                        color = if (selectedAmPm == "PM") Color.White else Color(0xFF666666),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Empty space at bottom
            item {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "",
                        color = Color.Transparent,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        // Center selection indicator
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
                    .background(
                        Color(0xFF00FF00).copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp)
                    )
            )
        }
    }
}

@Composable
fun AnalogClockFace(
    selectedHour: Int,
    selectedMinute: Int,
    onHourSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(
                Color(0xFF1A1A1A),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Clock numbers
        for (i in 1..12) {
            val angle = (i - 3) * 30.0 // Start from 12 o'clock
            val x = (kotlin.math.cos(Math.toRadians(angle)) * 80).toFloat()
            val y = (kotlin.math.sin(Math.toRadians(angle)) * 80).toFloat()
            
            val isSelected = if (selectedHour == 0) i == 12 else i == selectedHour
            
            Box(
                modifier = Modifier
                    .offset(x.dp, y.dp)
                    .size(40.dp)
                    .background(
                        if (isSelected) Color(0xFF00FF00) else Color.Transparent,
                        CircleShape
                    )
                    .clickable { 
                        val hour = if (i == 12) 0 else i
                        onHourSelected(hour)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = i.toString(),
                    color = if (isSelected) Color.Black else Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Hour hand
        val hourAngle = (selectedHour - 3) * 30.0 + (selectedMinute * 0.5)
        val hourX = (kotlin.math.cos(Math.toRadians(hourAngle)) * 40).toFloat()
        val hourY = (kotlin.math.sin(Math.toRadians(hourAngle)) * 40).toFloat()
        
        Box(
            modifier = Modifier
                .offset(hourX.dp, hourY.dp)
                .size(4.dp, 40.dp)
                .background(Color(0xFF00FF00), RoundedCornerShape(2.dp))
        )
        
        // Center dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color(0xFF00FF00), CircleShape)
        )
    }
}

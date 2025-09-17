package com.pilltracker.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import android.provider.MediaStore
import android.graphics.Bitmap
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
                                    // Convert 24-hour format to 12-hour format for display
                                    try {
                                        val timeParts = time.split(":")
                                        if (timeParts.size == 2) {
                                            val hour = timeParts[0].toInt()
                                            val minute = timeParts[1].toInt()
                                            
                                            val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                                            val ampm = if (hour < 12) "AM" else "PM"
                                            val displayTime = String.format("%02d:%02d %s", displayHour, minute, ampm)
                                            
                                            Text(
                                                text = displayTime,
                                                color = Color.Black,
                                                fontSize = 16.sp
                                            )
                                        } else {
                                            Text(
                                                text = "Invalid Time",
                                                color = Red500,
                                                fontSize = 16.sp
                                            )
                                        }
                                    } catch (e: Exception) {
                                        Text(
                                            text = "Invalid Time",
                                            color = Red500,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                        
                        if (times.size > 1) {
                            IconButton(
                                onClick = {
                                    try {
                                        times = times.filterIndexed { i, _ -> i != index }
                                    } catch (e: Exception) {
                                        android.util.Log.e("AddPillModal", "Error removing time: ${e.message}")
                                    }
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
                        try {
                            times = times.toMutableList().apply { add("") }
                        } catch (e: Exception) {
                            android.util.Log.e("AddPillModal", "Error adding time slot: ${e.message}")
                        }
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
                            try {
                                if (name.isNotBlank()) {
                                    val validTimes = times.filter { it.isNotBlank() }
                                    if (validTimes.isNotEmpty()) {
                                        // Convert 24-hour format to 12-hour format for nextDose
                                        val firstTime = validTimes.first()
                                        val timeParts = firstTime.split(":")
                                        if (timeParts.size == 2) {
                                            val hour = timeParts[0].toInt()
                                            val minute = timeParts[1].toInt()
                                            
                                            val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                                            val ampm = if (hour < 12) "AM" else "PM"
                                            val displayTime = String.format("%02d:%02d %s", displayHour, minute, ampm)
                                            
                                            val pill = Pill(
                                                id = editPill?.id ?: 0,
                                                name = name.trim(),
                                                dosage = "1 tablet", // Default dosage
                                                times = validTimes,
                                                color = if (useImage) "" else color,
                                                imagePath = if (useImage) imagePath else "",
                                                nextDose = displayTime,
                                                frequency = frequency,
                                                customDays = if (frequency == "custom") customDays else emptyList(),
                                                taken = editPill?.taken ?: false
                                            )
                                            onAddPill(pill)
                                        } else {
                                            android.util.Log.e("AddPillModal", "Invalid time format: $firstTime")
                                        }
                                    } else {
                                        android.util.Log.e("AddPillModal", "No valid times provided")
                                    }
                                } else {
                                    android.util.Log.e("AddPillModal", "Pill name is blank")
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("AddPillModal", "Error creating pill: ${e.message}", e)
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

    // Native Android TimePickerDialog
    if (showTimePicker) {
        val context = LocalContext.current
        val currentTime24 = times.getOrElse(selectedTimeIndex) { "" }
        
        // Parse current time or default to 12:00
        val calendar = Calendar.getInstance()
        if (currentTime24.isNotBlank()) {
            try {
                val inputFormat = SimpleDateFormat("HH:mm", Locale.US)
                val date = inputFormat.parse(currentTime24)
                if (date != null) {
                    calendar.time = date
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, 12)
                    calendar.set(Calendar.MINUTE, 0)
                }
            } catch (e: Exception) {
                android.util.Log.e("AddPillModal", "Error parsing time: ${e.message}")
                calendar.set(Calendar.HOUR_OF_DAY, 12)
                calendar.set(Calendar.MINUTE, 0)
            }
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 12)
            calendar.set(Calendar.MINUTE, 0)
        }

        LaunchedEffect(showTimePicker) {
            try {
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        try {
                            val newTimes = times.toMutableList()
                            if (selectedTimeIndex < newTimes.size) {
                                newTimes[selectedTimeIndex] = String.format("%02d:%02d", hourOfDay, minute)
                                times = newTimes
                            }
                            showTimePicker = false
                        } catch (e: Exception) {
                            android.util.Log.e("AddPillModal", "Error updating time: ${e.message}")
                            showTimePicker = false
                        }
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false // 24-hour format
                )
                timePickerDialog.show()
            } catch (e: Exception) {
                android.util.Log.e("AddPillModal", "Error showing time picker: ${e.message}")
                showTimePicker = false
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
fun ColorOption(
    color: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorValue = when (color) {
        "blue" -> Blue600
        "red" -> Red500
        "green" -> Green600
        "orange" -> Orange600
        "purple" -> Purple600
        else -> Blue600
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(colorValue)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) Color.White else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ImageCaptureSection(
    imagePath: String,
    onImageCaptured: (String) -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Create image file directory - use internal storage for FileProvider compatibility
    val imageFile = remember {
        File(context.filesDir, "pill_images").apply {
            if (!exists()) mkdirs()
        }
    }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher with proper file handling
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        android.util.Log.d("AddPillModal", "Camera result: success=$success")
        if (success && imageUri != null) {
            try {
                // Copy the image from the URI to our file
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val finalFile = File(imageFile, "pill_camera_${timestamp}.jpg")
                
                context.contentResolver.openInputStream(imageUri!!)?.use { inputStream ->
                    finalFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                
                if (finalFile.exists() && finalFile.length() > 0) {
                    android.util.Log.d("AddPillModal", "Image saved successfully: ${finalFile.absolutePath}")
                    onImageCaptured(finalFile.absolutePath)
                } else {
                    android.util.Log.e("AddPillModal", "Failed to save image: file doesn't exist or is empty")
                }
            } catch (e: Exception) {
                android.util.Log.e("AddPillModal", "Error saving image: ${e.message}")
                e.printStackTrace()
            }
        } else {
            android.util.Log.e("AddPillModal", "Camera failed or imageUri is null")
        }
    }

    // Function to launch camera
    val launchCamera: () -> Unit = {
        try {
            android.util.Log.d("AddPillModal", "=== CAMERA LAUNCH DEBUG ===")
            android.util.Log.d("AddPillModal", "Image directory: ${imageFile.absolutePath}")
            android.util.Log.d("AddPillModal", "Directory exists: ${imageFile.exists()}")
            android.util.Log.d("AddPillModal", "Directory writable: ${imageFile.canWrite()}")
            
            // Ensure directory exists
            if (!imageFile.exists()) {
                val created = imageFile.mkdirs()
                android.util.Log.d("AddPillModal", "Directory created: $created")
            }
            
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val photoFile = File(imageFile, "pill_${timestamp}.jpg")
            android.util.Log.d("AddPillModal", "Photo file path: ${photoFile.absolutePath}")
            
            // Create the file safely
            if (!photoFile.exists()) {
                val created = photoFile.createNewFile()
                android.util.Log.d("AddPillModal", "File created: $created")
            }
            android.util.Log.d("AddPillModal", "File exists: ${photoFile.exists()}")
            
            // Generate URI with proper error handling
            imageUri = try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    photoFile
                )
            } catch (e: Exception) {
                android.util.Log.e("AddPillModal", "Error generating FileProvider URI: ${e.message}")
                null
            }
            
            if (imageUri != null) {
                android.util.Log.d("AddPillModal", "Generated URI: $imageUri")
                android.util.Log.d("AddPillModal", "Package name: ${context.packageName}")
                
                android.util.Log.d("AddPillModal", "Launching camera...")
                cameraLauncher.launch(imageUri)
                android.util.Log.d("AddPillModal", "Camera launch called")
            } else {
                android.util.Log.e("AddPillModal", "Failed to generate URI, cannot launch camera")
            }
        } catch (e: Exception) {
            android.util.Log.e("AddPillModal", "Error launching camera: ${e.message}")
            e.printStackTrace()
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        android.util.Log.d("AddPillModal", "Camera permission granted: $isGranted")
        if (isGranted) {
            launchCamera()
        } else {
            android.util.Log.w("AddPillModal", "Camera permission denied")
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val photoFile = File(imageFile, "pill_gallery_${timestamp}.jpg")
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    photoFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                if (photoFile.exists() && photoFile.length() > 0) {
                    android.util.Log.d("AddPillModal", "Gallery image saved: ${photoFile.absolutePath}")
                    onImageCaptured(photoFile.absolutePath)
                } else {
                    android.util.Log.e("AddPillModal", "Failed to save gallery image")
                }
            } catch (e: Exception) {
                android.util.Log.e("AddPillModal", "Error saving gallery image: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Show captured image if available
        if (imagePath.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Gray100)
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
                        contentDescription = "Captured image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "Error loading image",
                        color = Red500,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // Camera button
        OutlinedButton(
            onClick = {
                android.util.Log.d("AddPillModal", "=== CAMERA BUTTON CLICKED ===")
                android.util.Log.d("AddPillModal", "hasCameraPermission: $hasCameraPermission")
                android.util.Log.d("AddPillModal", "Context: $context")
                android.util.Log.d("AddPillModal", "Image directory: ${imageFile.absolutePath}")
                
                if (!hasCameraPermission) {
                    android.util.Log.d("AddPillModal", "Requesting camera permission...")
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                } else {
                    android.util.Log.d("AddPillModal", "Opening camera directly...")
                    launchCamera()
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
                    text = "Take Photo",
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

@Composable
fun CustomDaysPickerDialog(
    onDismiss: () -> Unit,
    onDaysSelected: (List<String>) -> Unit,
    selectedDays: List<String>
) {
    var tempSelectedDays by remember { mutableStateOf(selectedDays.toMutableList()) }
    
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

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
                    text = "Select Days",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray800,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(daysOfWeek.size) { index ->
                        val day = daysOfWeek[index]
                        val isSelected = tempSelectedDays.contains(day)
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    try {
                                        if (isSelected) {
                                            tempSelectedDays.remove(day)
                                        } else {
                                            tempSelectedDays.add(day)
                                        }
                                    } catch (e: Exception) {
                                        android.util.Log.e("AddPillModal", "Error toggling day selection: ${e.message}")
                                    }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    try {
                                        if (checked) {
                                            tempSelectedDays.add(day)
                                        } else {
                                            tempSelectedDays.remove(day)
                                        }
                                    } catch (e: Exception) {
                                        android.util.Log.e("AddPillModal", "Error changing checkbox: ${e.message}")
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Blue600,
                                    uncheckedColor = Gray400
                                )
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Text(
                                text = day,
                                fontSize = 16.sp,
                                color = if (isSelected) Blue600 else Gray700,
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            try {
                                onDismiss()
                            } catch (e: Exception) {
                                android.util.Log.e("AddPillModal", "Error dismissing dialog: ${e.message}")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            try {
                                onDaysSelected(tempSelectedDays)
                            } catch (e: Exception) {
                                android.util.Log.e("AddPillModal", "Error selecting days: ${e.message}")
                            }
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
                            "OK",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
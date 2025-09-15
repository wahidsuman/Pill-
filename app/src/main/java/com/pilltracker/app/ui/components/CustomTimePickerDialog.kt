package com.pilltracker.app.ui.components

import android.app.TimePickerDialog
import android.content.Context
import android.widget.NumberPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pilltracker.app.ui.theme.Blue600
import com.pilltracker.app.ui.theme.Gray400
import com.pilltracker.app.ui.theme.Gray50
import com.pilltracker.app.ui.theme.Gray800
import java.util.*

@Composable
fun CustomTimePickerDialog(
    initialHour: Int = 12,
    initialMinute: Int = 0,
    isAM: Boolean = true,
    onTimeSelected: (timeString: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }
    var selectedIsAM by remember { mutableStateOf(isAM) }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Select Time",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray800,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // Time Picker Wheels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hours Picker (1-12)
                    CustomNumberPicker(
                        values = (1..12).toList(),
                        selectedValue = selectedHour,
                        onValueChange = { selectedHour = it as Int },
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Colon
                    Text(
                        text = ":",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blue600,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    // Minutes Picker (0-59)
                    CustomNumberPicker(
                        values = (0..59).toList(),
                        selectedValue = selectedMinute,
                        onValueChange = { selectedMinute = it as Int },
                        modifier = Modifier.weight(1f)
                    )
                    
                    // AM/PM Picker
                    CustomNumberPicker(
                        values = listOf("AM", "PM"),
                        selectedValue = if (selectedIsAM) "AM" else "PM",
                        onValueChange = { selectedIsAM = it == "AM" },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Real-time preview of selected time
                Text(
                    text = String.format(
                        "%02d:%02d %s",
                        selectedHour,
                        selectedMinute,
                        if (selectedIsAM) "AM" else "PM"
                    ),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Blue600,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Cancel Button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // OK Button
                    Button(
                        onClick = {
                            val timeString = String.format(
                                "%02d:%02d %s",
                                selectedHour,
                                selectedMinute,
                                if (selectedIsAM) "AM" else "PM"
                            )
                            onTimeSelected(timeString)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
fun CustomNumberPicker(
    values: List<Any>,
    selectedValue: Any,
    onValueChange: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    
    AndroidView(
        factory = { ctx ->
            NumberPicker(ctx).apply {
                minValue = 0
                maxValue = values.size - 1
                displayedValues = values.map { it.toString() }.toTypedArray()
                
                // Set initial value
                val initialIndex = values.indexOf(selectedValue)
                if (initialIndex >= 0) {
                    value = initialIndex
                }
                
                // Set up value change listener
                setOnValueChangedListener { _, _, newVal ->
                    if (newVal in values.indices) {
                        onValueChange(values[newVal])
                    }
                }
                
                // Configure for wheel/spinner appearance
                wrapSelectorWheel = false
                descendantFocusability = android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS
                
                // Customize appearance
                setTextColor(Blue600.toArgb())
                setTextSize(20f)
                
                // Apply custom styling
                applyCustomStyling()
            }
        },
        update = { picker ->
            val newIndex = values.indexOf(selectedValue)
            if (newIndex >= 0 && picker.value != newIndex) {
                picker.value = newIndex
            }
        },
        modifier = modifier
    )
}

private fun NumberPicker.applyCustomStyling() {
    try {
        // Use reflection to access private fields for styling
        val selectorWheelPaintField = NumberPicker::class.java.getDeclaredField("mSelectorWheelPaint")
        selectorWheelPaintField.isAccessible = true
        val selectorWheelPaint = selectorWheelPaintField.get(this) as android.graphics.Paint
        
        // Set text color for center item (selected) - deep blue and bold
        selectorWheelPaint.color = Blue600.toArgb()
        selectorWheelPaint.textSize = 24f
        selectorWheelPaint.isFakeBoldText = true
        selectorWheelPaint.typeface = android.graphics.Typeface.DEFAULT_BOLD
        
        // Set text color for non-selected items - light gray
        val textColorField = NumberPicker::class.java.getDeclaredField("mTextColor")
        textColorField.isAccessible = true
        textColorField.set(this, Gray400.toArgb())
        
        // Set divider color to match the theme
        val dividerColorField = NumberPicker::class.java.getDeclaredField("mDividerColor")
        dividerColorField.isAccessible = true
        dividerColorField.set(this, Blue600.toArgb())
        
        // Set selection divider color
        val selectionDividerColorField = NumberPicker::class.java.getDeclaredField("mSelectionDividerColor")
        selectionDividerColorField.isAccessible = true
        selectionDividerColorField.set(this, Blue600.toArgb())
        
        // Set selection divider height
        val selectionDividerHeightField = NumberPicker::class.java.getDeclaredField("mSelectionDividerHeight")
        selectionDividerHeightField.isAccessible = true
        selectionDividerHeightField.set(this, 2)
        
    } catch (e: Exception) {
        // Fallback if reflection fails
        e.printStackTrace()
    }
}

// Extension function to convert Color to ARGB
private fun Color.toArgb(): Int {
    return (alpha * 255).toInt() shl 24 or
           (red * 255).toInt() shl 16 or
           (green * 255).toInt() shl 8 or
           (blue * 255).toInt()
}
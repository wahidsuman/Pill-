package com.pilltracker.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.pilltracker.app.ui.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Calendar",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Gray800,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Month Navigation
        MonthNavigationCard(
            currentMonth = currentMonth,
            onPreviousMonth = { 
                currentMonth = Calendar.getInstance().apply {
                    time = currentMonth.time
                    add(Calendar.MONTH, -1)
                }
            },
            onNextMonth = { 
                currentMonth = Calendar.getInstance().apply {
                    time = currentMonth.time
                    add(Calendar.MONTH, 1)
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Calendar Grid
        CalendarGrid(
            month = currentMonth,
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Selected Date Details
        SelectedDateDetails(selectedDate = selectedDate)
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun MonthNavigationCard(
    currentMonth: Calendar,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous Month",
                    tint = Blue600,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Text(
                text = getMonthYearString(currentMonth),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Gray800
            )
            
            IconButton(onClick = onNextMonth) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next Month",
                    tint = Blue600,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun CalendarGrid(
    month: Calendar,
    selectedDate: Calendar,
    onDateSelected: (Calendar) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Day headers
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                val dayHeaders = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                dayHeaders.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Gray600
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Calendar days
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(280.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val calendarDays = getCalendarDays(month)
                items(calendarDays) { day ->
                    CalendarDayItem(
                        day = day,
                        isSelected = isSameDay(day, selectedDate),
                        isCurrentMonth = day.get(Calendar.MONTH) == month.get(Calendar.MONTH),
                        isToday = isToday(day),
                        hasMedications = hasMedicationsOnDay(day),
                        onClick = { onDateSelected(day) }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDayItem(
    day: Calendar,
    isSelected: Boolean,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    hasMedications: Boolean,
    onClick: () -> Unit
) {
    val dayNumber = day.get(Calendar.DAY_OF_MONTH).toString()
    
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(
                color = when {
                    isSelected -> Blue600
                    isToday -> Blue100
                    else -> Color.Transparent
                },
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayNumber,
                fontSize = 14.sp,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isSelected -> Color.White
                    !isCurrentMonth -> Gray300
                    isToday -> Blue600
                    else -> Gray800
                }
            )
            
            if (hasMedications) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(
                            color = if (isSelected) Color.White else Green600,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
fun SelectedDateDetails(selectedDate: Calendar) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = getFormattedDate(selectedDate),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Gray800,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Sample medication schedule for the day
            val medications = getMedicationsForDay(selectedDate)
            
            if (medications.isNotEmpty()) {
                medications.forEach { medication ->
                    MedicationScheduleItem(
                        name = medication.name,
                        time = medication.time,
                        taken = medication.taken,
                        color = medication.color
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                Text(
                    text = "No medications scheduled for this day",
                    fontSize = 14.sp,
                    color = Gray500,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun MedicationScheduleItem(
    name: String,
    time: String,
    taken: Boolean,
    color: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color indicator
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
                .size(12.dp)
                .background(colorValue, CircleShape)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Gray800
            )
            Text(
                text = time,
                fontSize = 14.sp,
                color = Gray600
            )
        }
        
        Icon(
            imageVector = if (taken) Icons.Default.CheckCircle else Icons.Default.Schedule,
            contentDescription = if (taken) "Taken" else "Pending",
            tint = if (taken) Green600 else Orange600,
            modifier = Modifier.size(20.dp)
        )
    }
}

// Helper functions
private fun getMonthYearString(calendar: Calendar): String {
    val monthNames = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    return "${monthNames[calendar.get(Calendar.MONTH)]} ${calendar.get(Calendar.YEAR)}"
}

private fun getCalendarDays(month: Calendar): List<Calendar> {
    val days = mutableListOf<Calendar>()
    val firstDayOfMonth = Calendar.getInstance().apply {
        time = month.time
        set(Calendar.DAY_OF_MONTH, 1)
    }
    
    // Get the first day of the week for this month
    val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK)
    val startOffset = firstDayOfWeek - Calendar.SUNDAY
    
    // Add empty cells for days before the first day of the month
    repeat(startOffset) {
        days.add(Calendar.getInstance().apply { 
            time = firstDayOfMonth.time
            add(Calendar.DAY_OF_MONTH, -startOffset + it)
        })
    }
    
    // Add all days of the month
    val lastDayOfMonth = firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    repeat(lastDayOfMonth) { day ->
        days.add(Calendar.getInstance().apply {
            time = firstDayOfMonth.time
            set(Calendar.DAY_OF_MONTH, day + 1)
        })
    }
    
    return days
}

private fun isSameDay(day1: Calendar, day2: Calendar): Boolean {
    return day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR) &&
           day1.get(Calendar.DAY_OF_YEAR) == day2.get(Calendar.DAY_OF_YEAR)
}

private fun isToday(day: Calendar): Boolean {
    val today = Calendar.getInstance()
    return isSameDay(day, today)
}

private fun hasMedicationsOnDay(day: Calendar): Boolean {
    // Sample data - in real app, check database
    return day.get(Calendar.DAY_OF_MONTH) % 3 == 0
}

private fun getFormattedDate(calendar: Calendar): String {
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH)
    
    val dayNames = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    
    return "${dayNames[dayOfWeek - 1]}, ${monthNames[month]} $dayOfMonth"
}

private fun getMedicationsForDay(day: Calendar): List<MedicationSchedule> {
    // Sample data - in real app, query database
    return if (day.get(Calendar.DAY_OF_MONTH) % 3 == 0) {
        listOf(
            MedicationSchedule("Aspirin", "08:00 AM", true, "blue"),
            MedicationSchedule("Vitamin D", "12:00 PM", false, "orange"),
            MedicationSchedule("Calcium", "06:00 PM", false, "green")
        )
    } else {
        emptyList()
    }
}

data class MedicationSchedule(
    val name: String,
    val time: String,
    val taken: Boolean,
    val color: String
)
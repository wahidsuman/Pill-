package com.pilltracker.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pilltracker.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Statistics",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Gray800,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Today's Summary
        TodaySummaryCard()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Weekly Overview
        WeeklyOverviewCard()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Medication Adherence
        AdherenceCard()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quick Stats
        QuickStatsRow()
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun TodaySummaryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Summary",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray800
                )
                Icon(
                    imageVector = Icons.Default.Today,
                    contentDescription = "Today",
                    tint = Blue600,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Taken",
                    value = "3",
                    color = Green600,
                    icon = Icons.Default.CheckCircle
                )
                StatItem(
                    label = "Missed",
                    value = "1",
                    color = Red500,
                    icon = Icons.Default.Cancel
                )
                StatItem(
                    label = "Pending",
                    value = "2",
                    color = Orange600,
                    icon = Icons.Default.Schedule
                )
            }
        }
    }
}

@Composable
fun WeeklyOverviewCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "This Week",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray800
                )
                Icon(
                    imageVector = Icons.Default.CalendarViewWeek,
                    contentDescription = "Week",
                    tint = Blue600,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Weekly adherence bar
            WeeklyAdherenceBar()
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "85% Adherence Rate",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Green600
            )
        }
    }
}

@Composable
fun WeeklyAdherenceBar() {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val adherence = listOf(100, 80, 100, 60, 100, 100, 85) // Percentage for each day
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        days.forEachIndexed { index, day ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height((adherence[index] * 0.8).dp)
                        .background(
                            color = if (adherence[index] >= 80) Green600 else 
                                   if (adherence[index] >= 60) Orange600 else Red500,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = day,
                    fontSize = 12.sp,
                    color = Gray600
                )
            }
        }
    }
}

@Composable
fun AdherenceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Medication Adherence",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray800
                )
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Trend",
                    tint = Blue600,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Adherence progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .background(
                            color = Gray200,
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.85f)
                            .background(
                                color = Green600,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "85%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Green600
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Great job! You're maintaining excellent adherence.",
                fontSize = 14.sp,
                color = Gray600
            )
        }
    }
}

@Composable
fun QuickStatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickStatCard(
            title = "Streak",
            value = "7 days",
            icon = Icons.Default.LocalFireDepartment,
            color = Orange600,
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            title = "Total Pills",
            value = "42",
            icon = Icons.Default.Medication,
            color = Blue600,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Gray800
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = Gray600
            )
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Gray600
        )
    }
}
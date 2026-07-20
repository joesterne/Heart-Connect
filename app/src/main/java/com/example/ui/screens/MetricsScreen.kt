package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.navigation.LocalOpenDrawer
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricsScreen(
    onBack: () -> Unit
) {
    val openDrawer = LocalOpenDrawer.current
    
    // Heart Rate data (BPM)
    val heartRateModel = remember { entryModelOf(72f, 75f, 73f, 78f, 71f, 69f, 74f) }
    
    // Recovery milestones progress (Days post-op)
    val activityModel = remember { entryModelOf(500f, 1200f, 2500f, 3200f, 4100f, 5000f, 5800f) }

    // Blood pressure systolic
    val bpModel = remember { entryModelOf(120f, 122f, 118f, 121f, 119f, 120f, 118f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Metrics Dashboard") },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Track your post-transplant recovery milestones and daily health metrics.", 
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            MetricCard(title = "Heart Rate (BPM)", subtitle = "Last 7 Days") {
                Chart(
                    chart = lineChart(),
                    model = heartRateModel,
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis()
                )
            }
            
            MetricCard(title = "Daily Steps (Activity)", subtitle = "Gradual Increase in Activity") {
                Chart(
                    chart = columnChart(),
                    model = activityModel,
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis()
                )
            }
            
            MetricCard(title = "Blood Pressure (Systolic)", subtitle = "Last 7 Days") {
                Chart(
                    chart = lineChart(),
                    model = bpModel,
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis()
                )
            }
        }
    }
}

@Composable
fun MetricCard(title: String, subtitle: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

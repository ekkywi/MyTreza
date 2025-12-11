package com.trezanix.mytreza.presentation.features.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.hilt.navigation.compose.hiltViewModel
import com.trezanix.mytreza.presentation.theme.AccentGreen
import com.trezanix.mytreza.presentation.theme.AccentRed
import com.trezanix.mytreza.presentation.theme.BrandBlue
import com.trezanix.mytreza.presentation.util.formatRupiah
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material.icons.filled.Info

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val categoryStats by viewModel.categoryStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val monthFormatter = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadAnalysisData() // Panggil fungsi refresh
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analisis Keuangan", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F7FA))
            )
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 1. MONTH SELECTOR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.changeMonth(-1) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
                Text(
                    text = monthFormatter.format(selectedDate),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { viewModel.changeMonth(1) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. SUMMARY CARDS (Scrollable Content starts here)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // A. Financial Summary Cards
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SummaryCard(
                            title = "Pemasukan",
                            amount = summary.income,
                            color = AccentGreen,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            title = "Pengeluaran",
                            amount = summary.expense,
                            color = AccentRed,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    // Net Flow Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandBlue)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Sisa Bulan Ini (Net)", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(formatRupiah(summary.net), color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // B. Chart Placeholder (Nanti kita pasang Vico di sini)
                item {
                    Text("Pengeluaran per Kategori", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator()
                        } else if (categoryStats.isEmpty()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Belum ada pengeluaran bulan ini", color = Color.Gray)
                            }
                        } else {
                            DonutChart(
                                data = categoryStats,
                                modifier = Modifier.size(200.dp) // Ukuran lingkaran
                            )
                        }
                    }
                }

                // C. Category List Detail
                items(categoryStats) { stat ->
                    CategoryStatItem(stat)
                }

                item { Spacer(modifier = Modifier.height(80.dp)) } // Bottom padding
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, amount: Double, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                formatRupiah(amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun CategoryStatItem(stat: CategoryStat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(android.graphics.Color.parseColor(stat.colorHex))) // Parse String Hex
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(stat.categoryName, fontWeight = FontWeight.Medium)
        }
        Text(formatRupiah(stat.total), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DonutChart(
    data: List<CategoryStat>,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.total }

    // Animasi Chart (Opsional, tapi biar keren)
    // Untuk simplifikasi kita render statis dulu

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 40.dp.toPx()
            val radius = size.minDimension / 2 - strokeWidth / 2
            val center = center

            var startAngle = -90f // Mulai dari jam 12

            data.forEach { stat ->
                val sweepAngle = (stat.total / total).toFloat() * 360f

                // Parse warna dari Hex String
                val color = try {
                    Color(android.graphics.Color.parseColor(stat.colorHex))
                } catch (e: Exception) {
                    Color.Gray
                }

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = strokeWidth,
                        cap = androidx.compose.ui.graphics.StrokeCap.Butt
                    )
                )

                startAngle += sweepAngle
            }
        }

        // Teks di tengah Donut
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = formatRupiah(total),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BrandBlue
            )
        }
    }
}
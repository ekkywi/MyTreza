package com.trezanix.mytreza.presentation.features.wallet.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trezanix.mytreza.data.remote.dto.TransactionDto
import com.trezanix.mytreza.presentation.features.wallet.WalletCard
import com.trezanix.mytreza.presentation.theme.BrandBlue
import com.trezanix.mytreza.presentation.util.formatRupiah
import java.text.DateFormatSymbols
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDetailScreen(
    onNavigateUp: () -> Unit,
    viewModel: WalletDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // State untuk Month Picker UI
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Dompet", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opsi")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Dompet") },
                            onClick = { showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus Dompet", color = Color.Red) },
                            onClick = { showMenu = false }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F7FA)
                )
            )
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is WalletDetailViewModel.DetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is WalletDetailViewModel.DetailState.Error -> {
                    Text(s.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
                is WalletDetailViewModel.DetailState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // 1. KARTU DOMPET
                        item { WalletCard(wallet = s.wallet) }

                        // 2. --- FITUR BARU: MONTH PICKER ---
                        item {
                            MonthPicker(
                                month = selectedMonth,
                                year = selectedYear,
                                onPrev = {
                                    // Logic mundur bulan
                                    var m = selectedMonth - 1
                                    var y = selectedYear
                                    if (m < 1) { m = 12; y-- }
                                    viewModel.changeMonth(m, y)
                                },
                                onNext = {
                                    // Logic maju bulan
                                    var m = selectedMonth + 1
                                    var y = selectedYear
                                    if (m > 12) { m = 1; y++ }
                                    viewModel.changeMonth(m, y)
                                }
                            )
                        }

                        // 3. --- MINI DASHBOARD (STATS) ---
                        item {
                            WalletMonthlySummary(income = s.stats.income, expense = s.stats.expense)
                        }

                        // 4. JUDUL RIWAYAT
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Riwayat Transaksi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // 5. LIST TRANSAKSI
                        if (s.transactions.isEmpty()) {
                            item {
                                Text(
                                    "Tidak ada transaksi di periode ini.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 20.dp)
                                )
                            }
                        } else {
                            items(s.transactions) { trx ->
                                TransactionItem(trx)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- KOMPONEN BARU: MONTH PICKER ---
@Composable
fun MonthPicker(
    month: Int,
    year: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    // Ubah angka bulan (1-12) jadi nama (Januari, dst)
    val monthName = DateFormatSymbols().months[month - 1]

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrev) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null)
        }

        Text(
            text = "$monthName $year",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = BrandBlue
        )

        IconButton(onClick = onNext) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
        }
    }
}

// --- KOMPONEN STATS ---
@Composable
fun WalletMonthlySummary(income: Double, expense: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SummaryCard(
            title = "Pemasukan",
            amount = income,
            color = Color(0xFF4CAF50),
            icon = Icons.Default.ArrowDownward,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            title = "Pengeluaran",
            amount = expense,
            color = Color.Red,
            icon = Icons.Default.ArrowUpward,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryCard(title: String, amount: Double, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(24.dp).background(color.copy(alpha = 0.1f), RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = formatRupiah(amount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun TransactionItem(trx: TransactionDto) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(trx.description ?: "Tanpa Keterangan", fontWeight = FontWeight.Bold)
                Text(trx.date.take(10), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            val isExpense = trx.type == "EXPENSE"
            val color = if (isExpense) Color.Red else Color(0xFF4CAF50)
            val prefix = if (isExpense) "- " else "+ "
            Text(text = "$prefix${formatRupiah(trx.amount)}", color = color, fontWeight = FontWeight.Bold)
        }
    }
}
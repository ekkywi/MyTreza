package com.trezanix.mytreza.presentation.features.wallet.detail

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trezanix.mytreza.data.remote.dto.TransactionDto
import com.trezanix.mytreza.presentation.features.wallet.WalletCard
import com.trezanix.mytreza.presentation.theme.BrandBlue
import com.trezanix.mytreza.presentation.util.formatRupiah
import java.text.DateFormatSymbols
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDetailScreen(
    onNavigateUp: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: WalletDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }



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
                            onClick = {
                                showMenu = false
                                if (state is WalletDetailViewModel.DetailState.Success) {
                                    val id = (state as WalletDetailViewModel.DetailState.Success).wallet.id
                                    onNavigateToEdit(id)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus Dompet", color = Color.Red) },
                            onClick = {
                                showMenu = false
                                showDeleteDialog = true
                            }
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
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
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
                        item { WalletCard(wallet = s.wallet) }

                        item {
                            MonthPicker(
                                month = selectedMonth,
                                year = selectedYear,
                                onPrev = {
                                    var m = selectedMonth - 1
                                    var y = selectedYear
                                    if (m < 1) { m = 12; y-- }
                                    viewModel.changeMonth(m, y)
                                },
                                onNext = {
                                    var m = selectedMonth + 1
                                    var y = selectedYear
                                    if (m > 12) { m = 1; y++ }
                                    viewModel.changeMonth(m, y)
                                }
                            )
                        }

                        item {
                            WalletMonthlySummary(income = s.stats.income, expense = s.stats.expense)
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Riwayat Transaksi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Hapus Dompet?") },
            text = {
                Text("Tindakan ini tidak dapat dibatalkan. Riwayat transaksi mungkin akan terpengaruh.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteWallet(onSuccess = {
                            onNavigateUp()
                        })
                    }
                ) {
                    Text("Hapus", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = BrandBlue)
                }
            },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.Gray
        )
    }
}

@Composable
fun MonthPicker(month: Int, year: Int, onPrev: () -> Unit, onNext: () -> Unit) {
    val monthName = DateFormatSymbols().months[month - 1]
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrev) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null) }
        Text(text = "$monthName $year", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandBlue)
        IconButton(onClick = onNext) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) }
    }
}

@Composable
fun WalletMonthlySummary(income: Double, expense: Double) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        SummaryCard("Pemasukan", income, Color(0xFF4CAF50), Icons.Default.ArrowDownward, Modifier.weight(1f))
        SummaryCard("Pengeluaran", expense, Color.Red, Icons.Default.ArrowUpward, Modifier.weight(1f))
    }
}

@Composable
fun SummaryCard(title: String, amount: Double, color: Color, icon: ImageVector, modifier: Modifier = Modifier) {
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
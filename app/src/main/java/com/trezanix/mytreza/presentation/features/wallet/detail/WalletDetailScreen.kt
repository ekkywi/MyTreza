package com.trezanix.mytreza.presentation.features.wallet.detail

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.trezanix.mytreza.domain.model.Transaction // Import Domain Transaction
import com.trezanix.mytreza.presentation.components.WalletCard
import com.trezanix.mytreza.presentation.theme.AccentGreen
import com.trezanix.mytreza.presentation.theme.AccentRed
import com.trezanix.mytreza.presentation.theme.BrandBlue
import com.trezanix.mytreza.presentation.util.formatRupiah
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.trezanix.mytreza.presentation.util.getCategoryIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDetailScreen(
    onNavigateUp: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: WalletDetailViewModel = hiltViewModel()
) {
    // Collect Data (Sekarang isinya Domain Model)
    val wallet by viewModel.wallet.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var showActionSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }


    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
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

    LaunchedEffect(deleteState) {
        when (deleteState) {
            true -> {
                Toast.makeText(context, "Dompet berhasil dihapus", Toast.LENGTH_SHORT).show()
                onNavigateUp()
                viewModel.resetDeleteState()
            }
            false -> {
                Toast.makeText(context, "Gagal menghapus dompet", Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteState()
            }
            else -> {}
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Dompet?") },
            text = { Text("Apakah Anda yakin ingin menghapus dompet ini? Semua data transaksi terkait akan hilang.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteWallet()
                    }
                ) {
                    Text("Hapus", color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showActionSheet) {
        ModalBottomSheet(
            onDismissRequest = { showActionSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(modifier = Modifier.padding(bottom = 48.dp)) {
                Text(
                    text = "Aksi Dompet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                ListItem(
                    headlineContent = { Text("Edit / Arsipkan") },
                    leadingContent = {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = BrandBlue)
                    },
                    modifier = Modifier.clickable {
                        showActionSheet = false
                        wallet?.id?.let { onNavigateToEdit(it) }
                    }
                )

                ListItem(
                    headlineContent = { Text("Hapus Dompet", color = AccentRed) },
                    leadingContent = {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = AccentRed)
                    },
                    modifier = Modifier.clickable {
                        showActionSheet = false
                        if (transactions.isNotEmpty()) {
                            Toast.makeText(context, "Dompet memiliki transaksi, tidak dapat dihapus. Silakan gunakan opsi Arsip.", Toast.LENGTH_LONG).show()
                        } else {
                            showDeleteDialog = true
                        }
                    }
                )
            }
        }
    }

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
                    IconButton(onClick = { showActionSheet = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opsi")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F7FA))
            )
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {

            if (wallet == null && isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = BrandBlue)
            } else if (wallet != null) {
                LazyColumn(
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 1. KARTU DOMPET
                    item {
                        // wallet sudah Domain Model, tidak perlu dikonversi lagi
                        WalletCard(wallet = wallet!!)
                    }

                    // 2. FILTER BULAN
                    item {
                        MonthPicker(
                            currentDate = selectedDate,
                            onPrev = { viewModel.prevMonth() },
                            onNext = { viewModel.nextMonth() }
                        )
                    }

                    // 3. STATISTIK BULANAN
                    item {
                        WalletMonthlySummary(
                            income = stats?.totalIncome ?: 0.0,
                            expense = stats?.totalExpense ?: 0.0
                        )
                    }

                    // 4. HEADER TRANSAKSI
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Riwayat Transaksi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 5. LIST TRANSAKSI
                    if (isLoading && transactions.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = BrandBlue, modifier = Modifier.size(24.dp))
                            }
                        }
                    } else if (transactions.isEmpty()) {
                        item {
                            EmptyStateMessage()
                        }
                    } else {
                        items(transactions) { trx ->
                            TransactionItemAesthetic(trx)
                        }
                    }
                }
            } else {
                Text("Dompet tidak ditemukan", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
            }
        }
    }
}

// --- SUB COMPONENTS ---

@Composable
fun MonthPicker(currentDate: Calendar, onPrev: () -> Unit, onNext: () -> Unit) {
    val formattedDate = SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).format(currentDate.time)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrev) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = BrandBlue)
        }
        Text(text = formattedDate, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandBlue)
        IconButton(onClick = onNext) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = BrandBlue)
        }
    }
}

@Composable
fun WalletMonthlySummary(income: Double, expense: Double) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        SummaryCard("Pemasukan", income, AccentGreen, Icons.Default.ArrowDownward, Modifier.weight(1f))
        SummaryCard("Pengeluaran", expense, AccentRed, Icons.Default.ArrowUpward, Modifier.weight(1f))
    }
}

@Composable
fun SummaryCard(title: String, amount: Double, color: Color, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(28.dp).background(color.copy(alpha = 0.15f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = formatRupiah(amount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color, maxLines = 1)
        }
    }
}

@Composable
fun TransactionItemAesthetic(trx: Transaction) { // PENTING: Tipe datanya Transaction (Domain)
    val isExpense = trx.type == "EXPENSE"
    val amountColor = if (isExpense) AccentRed else AccentGreen
    val iconBgColor = if (isExpense) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
    val icon = if (isExpense) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
    val prefix = if (isExpense) "- " else "+ "

    val dateReadable = try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val formatter = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))
        val parsed = parser.parse(trx.date) ?: Date()
        formatter.format(parsed)
    } catch (e: Exception) { "-" }



    // Logic category icon
    val isTransfer = trx.categoryName.contains("Transfer", true)
    
    // Prioritaskan icon dari database jika ada
    // Prioritaskan icon dari database jika ada
    val databaseIcon = if (trx.categoryIcon != null) getCategoryIcon(trx.categoryIcon) else null
    
    // Fallback logic
    val displayIcon = databaseIcon ?: (if (isTransfer) Icons.Default.SwapHoriz else icon)

    // Dynamic Color Logic
    val displayColor = try {
        if (trx.categoryColor != null) Color(android.graphics.Color.parseColor(trx.categoryColor)) else amountColor
    } catch (e: Exception) { amountColor }

    val displayBgColor = if (trx.categoryColor != null) displayColor.copy(alpha = 0.1f) else iconBgColor

    Row(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(displayBgColor), contentAlignment = Alignment.Center) {
            Icon(imageVector = displayIcon, contentDescription = null, tint = displayColor, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = trx.categoryName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = dateReadable, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text(text = "$prefix${formatRupiah(trx.amount)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = amountColor)
    }
}

@Composable
fun EmptyStateMessage() {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Tidak ada transaksi di periode ini.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
    }
}
package com.trezanix.mytreza.presentation.features.dashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.trezanix.mytreza.data.remote.dto.TransactionDto
import com.trezanix.mytreza.domain.model.Transaction
import com.trezanix.mytreza.presentation.features.transaction.detail.TransactionDetailSheet // Pastikan import ini ada
import com.trezanix.mytreza.presentation.theme.AccentGreen
import com.trezanix.mytreza.presentation.theme.AccentRed
import com.trezanix.mytreza.presentation.theme.BrandBlue
import com.trezanix.mytreza.presentation.theme.BrandBlueDark
import com.trezanix.mytreza.presentation.util.formatRupiah
import com.trezanix.mytreza.presentation.util.getGreetingMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.trezanix.mytreza.presentation.util.getCategoryIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToEditTransaction: (Transaction) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    // --- STATE COLLECTORS ---
    val totalBalance by viewModel.totalBalance.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState() // [NEW] Refresh State
    val message by viewModel.message.collectAsState() // Pesan Toast (Sukses/Error)

    // --- LOCAL STATE ---
    // Menyimpan transaksi yang sedang diklik untuk ditampilkan di BottomSheet
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val userName by viewModel.userName.collectAsState()


    // --- SIDE EFFECTS ---

    // 1. Tampilkan Toast jika ada pesan dari ViewModel
    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    // 2. Auto Refresh saat kembali ke halaman ini
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Jangan loadData lagi jika sedang refreshing manual, tapi biasanya aman
                viewModel.loadData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        // [NEW] Wrapper PullToRefreshBox
        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // 1. HEADER (Saldo)
                item {
                    DashboardHeader(
                        totalBalance = totalBalance,
                        isLoading = isLoading,
                        userName = userName
                    )
                }

                // 2. QUICK ACTIONS
                item {
                    Column(
                        modifier = Modifier
                            .offset(y = (-30).dp)
                            .padding(horizontal = 24.dp)
                    ) {
                        QuickActionsGrid()
                    }
                }

                // 3. TITLE TRANSAKSI
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Transaksi Terakhir",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Lihat Semua",
                            style = MaterialTheme.typography.labelMedium,
                            color = BrandBlue,
                            modifier = Modifier.clickable { /* TODO: Navigate to History */ }
                        )
                    }
                }

                // 4. LIST TRANSAKSI
                if (isLoading && recentTransactions.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = BrandBlue, modifier = Modifier.size(32.dp))
                        }
                    }
                } else if (recentTransactions.isEmpty()) {
                    item {
                        EmptyTransactionState()
                    }
                } else {
                    items(recentTransactions) { trxDto ->
                        // Wrapper Box untuk menangani Klik Item
                        Box(modifier = Modifier.clickable {
                            // Konversi DTO ke Domain Model saat diklik
                            selectedTransaction = trxDto.toDomain()
                        }) {
                            TransactionItem(transaction = trxDto)
                        }
                    }
                }
            }
        }
    }

// --- 5. BOTTOM SHEET DETAIL ---
    if (selectedTransaction != null) {
        TransactionDetailSheet(
            transaction = selectedTransaction!!,
            onDismiss = { selectedTransaction = null },
            onEdit = {
                selectedTransaction?.let { trx ->
                    onNavigateToEditTransaction(trx)
                }
                selectedTransaction = null
            },
            onDelete = {
                // UPDATE: Jangan langsung hapus, tapi munculkan Dialog
                showDeleteDialog = true
                // (Jangan set selectedTransaction = null dulu, biar sheet tetap ada di background atau tutup sesuai selera)
            }
        )
    }

    // --- 6. DIALOG KONFIRMASI HAPUS (TAMBAHAN BARU) ---
    if (showDeleteDialog && selectedTransaction != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Transaksi?") },
            text = { Text("Data yang dihapus tidak dapat dikembalikan. Jika ini transaksi Transfer, pasangannya juga akan terhapus.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // EKSEKUSI HAPUS
                        selectedTransaction?.id?.let { id ->
                            viewModel.deleteTransaction(id)
                        }
                        showDeleteDialog = false
                        selectedTransaction = null // Tutup BottomSheet juga
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = AccentRed)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// --- HELPER MAPPER (DTO -> DOMAIN) ---
// Fungsi ini mengubah data mentah API menjadi data bersih untuk UI Detail
fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        id = this.id,
        amount = this.amount,
        description = this.description,
        date = this.date,
        type = this.type,
        categoryName = this.category?.name ?: "Umum",
        walletName = this.wallet?.name ?: "Dompet",
        categoryId = this.categoryId,
        walletId = this.walletId,
        categoryIcon = this.category?.icon,
        categoryColor = this.category?.color
    )
}

// --- SUB COMPONENTS (UI ELEMENTS) ---

@Composable
fun DashboardHeader(totalBalance: Double, isLoading: Boolean, userName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.verticalGradient(colors = listOf(BrandBlue, BrandBlueDark)))
    ) {
        Box(
            modifier = Modifier
                .offset(x = 200.dp, y = (-50).dp)
                .size(300.dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize().padding(24.dp).padding(top = 10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(48.dp).border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape).padding(4.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.AccountCircle, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(getGreetingMessage(), style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                        Text(text = userName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                IconButton(onClick = {}, modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape).size(40.dp)) {
                    Icon(Icons.Default.Notifications, "Notifikasi", tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(36.dp))
            Text("Total Saldo Aktif", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(formatRupiah(totalBalance), style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp), color = Color.White)
                }
            }
        }
    }
}

@Composable
fun QuickActionsGrid() {
    Card(modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(24.dp), spotColor = BrandBlue.copy(alpha = 0.1f)), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(0.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            QuickActionItem(Icons.Default.QrCodeScanner, "Scan")
            QuickActionItem(Icons.Default.SwapHoriz, "Transfer")
            QuickActionItem(Icons.Default.History, "Riwayat")
            QuickActionItem(Icons.Default.MoreHoriz, "Lainnya")
        }
    }
}

@Composable
fun QuickActionItem(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(56.dp).background(Color(0xFFF5F7FA), CircleShape).clip(CircleShape).clickable { }, contentAlignment = Alignment.Center) {
            Icon(icon, label, tint = BrandBlue, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun TransactionItem(transaction: TransactionDto) {
    val isExpense = transaction.type == "EXPENSE"
    val amountColor = if (isExpense) AccentRed else AccentGreen
    val iconBgColor = if (isExpense) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
    val icon = if (isExpense) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
    val prefix = if (isExpense) "- " else "+ "

    val dateReadable = try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val formatter = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))
        val parsed = parser.parse(transaction.date) ?: Date()
        formatter.format(parsed)
    } catch (e: Exception) { "-" }

    val walletName = transaction.wallet?.name ?: "Dompet"
    val isTransfer = transaction.category?.name?.contains("Transfer", true) == true
    
    // Dynamic Icon Logic
    // Dynamic Icon Logic
    val categoryIconName = transaction.category?.icon
    val categoryColorHex = transaction.category?.color
    
    val databaseIcon = if (categoryIconName != null) getCategoryIcon(categoryIconName) else null
    val displayIcon = databaseIcon ?: (if (isTransfer) Icons.Default.SwapHoriz else icon)

    // Dynamic Color Logic
    val displayColor = try {
        if (categoryColorHex != null) Color(android.graphics.Color.parseColor(categoryColorHex)) else amountColor
    } catch (e: Exception) { amountColor }
    
    val displayBgColor = if (categoryColorHex != null) displayColor.copy(alpha = 0.1f) else iconBgColor

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).background(Color.White, RoundedCornerShape(16.dp)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(displayBgColor), contentAlignment = Alignment.Center) {
            Icon(displayIcon, null, tint = displayColor, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.category?.name ?: transaction.description ?: "Transaksi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text("$walletName • $dateReadable", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text("$prefix${formatRupiah(transaction.amount)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = amountColor)
    }
}

@Composable
fun EmptyTransactionState() {
    Column(modifier = Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.History, null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Belum ada transaksi terbaru", color = Color.Gray)
    }
}
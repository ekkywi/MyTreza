package com.trezanix.mytreza.presentation.features.dashboard

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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.trezanix.mytreza.data.remote.dto.TransactionDto
import com.trezanix.mytreza.presentation.theme.AccentGreen
import com.trezanix.mytreza.presentation.theme.AccentRed
import com.trezanix.mytreza.presentation.theme.BrandBlue
import com.trezanix.mytreza.presentation.theme.BrandBlueDark
import com.trezanix.mytreza.presentation.util.formatRupiah
import com.trezanix.mytreza.presentation.util.getGreetingMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    // Collect Data Real dari ViewModel
    val totalBalance by viewModel.totalBalance.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // --- AUTO REFRESH LOGIC ---
    // Agar saldo update otomatis saat kembali dari halaman lain
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

    Scaffold(
        containerColor = Color(0xFFF5F7FA) // Background abu-abu sangat muda yang bersih
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp) // Space untuk BottomNav
        ) {
            // 1. HEADER (Gradient & Saldo)
            item {
                DashboardHeader(
                    totalBalance = totalBalance,
                    isLoading = isLoading
                )
            }

            // 2. QUICK ACTIONS (Menu Cepat)
            item {
                Column(
                    modifier = Modifier
                        .offset(y = (-30).dp) // Efek menumpuk sedikit ke header
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
                        modifier = Modifier.clickable { /* Navigate to History */ }
                    )
                }
            }

            // 4. LIST TRANSAKSI (Real Data)
            if (isLoading && recentTransactions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BrandBlue)
                    }
                }
            } else if (recentTransactions.isEmpty()) {
                item {
                    EmptyTransactionState()
                }
            } else {
                items(recentTransactions) { trx ->
                    TransactionItem(trx)
                }
            }
        }
    }
}

@Composable
fun DashboardHeader(
    totalBalance: Double,
    isLoading: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp) // Sedikit lebih pendek agar proporsional
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BrandBlue, BrandBlueDark)
                )
            )
    ) {
        // Hiasan Circle Transparan (Aesthetic touch)
        Box(
            modifier = Modifier
                .offset(x = 200.dp, y = (-50).dp)
                .size(300.dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(top = 10.dp) // Safe area
        ) {
            // Top Row: Profile & Notif
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Profile Icon dengan Border Putih Tipis
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = getGreetingMessage(), // Sapaan Dinamis
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Kawan MyTreza", // Nama User (Bisa diambil dari pref nanti)
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifikasi",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Balance Section
            Text(
                text = "Total Saldo Aktif",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = formatRupiah(totalBalance),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionsGrid() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = BrandBlue.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionItem(icon = Icons.Default.QrCodeScanner, label = "Scan")
            QuickActionItem(icon = Icons.Default.SwapHoriz, label = "Transfer")
            QuickActionItem(icon = Icons.Default.History, label = "Riwayat")
            QuickActionItem(icon = Icons.Default.MoreHoriz, label = "Lainnya")
        }
    }
}

@Composable
fun QuickActionItem(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color(0xFFF5F7FA), CircleShape) // Warna background icon soft
                .clip(CircleShape)
                .clickable { /* TODO */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = BrandBlue,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun TransactionItem(trx: TransactionDto) {
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
    } catch (e: Exception) {
        "-"
    }

    // Mengambil Nama Wallet (jika ada relasi di DTO) atau default
    val walletName = trx.wallet?.name ?: "Dompet"

    // Logic Icon Transfer
    val isTransfer = trx.category?.name?.contains("Transfer", true) == true
    val displayIcon = if (isTransfer) Icons.Default.SwapHoriz else icon

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Box
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = displayIcon,
                contentDescription = null,
                tint = amountColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Text Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = trx.category?.name ?: trx.description ?: "Transaksi",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$walletName • $dateReadable",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        // Amount
        Text(
            text = "$prefix${formatRupiah(trx.amount)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}

@Composable
fun EmptyTransactionState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Belum ada transaksi terbaru", color = Color.Gray)
    }
}
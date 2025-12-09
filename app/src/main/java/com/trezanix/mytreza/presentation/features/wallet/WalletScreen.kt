package com.trezanix.mytreza.presentation.features.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.trezanix.mytreza.domain.model.Wallet
import com.trezanix.mytreza.presentation.theme.BrandBlue
import com.trezanix.mytreza.presentation.theme.BrandBlueDark
import com.trezanix.mytreza.presentation.util.formatRupiah

@Composable
fun WalletScreen(
    viewModel: WalletViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadWallets()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Dompet Saya",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Kelola semua sumber danamu disini",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (val s = state) {
            is WalletViewModel.WalletState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandBlue)
                }
            }
            is WalletViewModel.WalletState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = s.message, color = Color.Red)
                }
            }
            is WalletViewModel.WalletState.Success -> {
                if (s.wallets.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada dompet. Tambah dulu yuk!", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(s.wallets) { wallet ->
                            Box(modifier = Modifier.clickable { onNavigateToDetail(wallet.id) }) {
                                WalletCard(wallet)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WalletCard(wallet: Wallet) {
    val defaultColor = when (wallet.type.uppercase()) {
        "CASH" -> Color(0xFF4CAF50)
        "EWALLET" -> Color(0xFFFF9800)
        "BANK" -> BrandBlue
        "SAVING" -> Color(0xFF9C27B0)
        "FAMILY" -> Color(0xFFE91E63)
        "ASSET" -> Color(0xFF607D8B)
        else -> BrandBlue
    }

    val baseColor = if (!wallet.color.isNullOrBlank()) {
        try {
            Color(android.graphics.Color.parseColor(wallet.color))
        } catch (e: Exception) {
            defaultColor
        }
    } else {
        defaultColor
    }

    val icon = when (wallet.type.uppercase()) {
        "CASH" -> Icons.Rounded.AttachMoney
        "EWALLET" -> Icons.Rounded.AccountBalanceWallet
        "BANK" -> Icons.Rounded.AccountBalance
        "SAVING" -> Icons.Rounded.Star
        "FAMILY" -> Icons.Rounded.Group
        "ASSET" -> Icons.Rounded.Apartment
        else -> Icons.Rounded.AccountBalance
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth().height(160.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(baseColor, baseColor.copy(alpha = 0.7f))
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = wallet.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column {
                    Text(
                        text = "Saldo Aktif",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatRupiah(wallet.balance),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = wallet.type.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

fun getDefaultColorByType(type: String): Color {
    return when (type.uppercase()) {
        "CASH" -> Color(0xFF4CAF50)
        "EWALLET" -> Color(0xFFFFA726)
        else -> BrandBlue
    }
}
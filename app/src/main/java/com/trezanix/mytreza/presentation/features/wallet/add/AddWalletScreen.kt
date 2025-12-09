package com.trezanix.mytreza.presentation.features.wallet.add

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trezanix.mytreza.domain.model.Wallet
import com.trezanix.mytreza.presentation.features.wallet.WalletCard
import com.trezanix.mytreza.presentation.theme.BrandBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWalletScreen(
    onNavigateUp: () -> Unit,
    viewModel: AddWalletViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val name by viewModel.walletName.collectAsState()
    val type by viewModel.walletType.collectAsState()
    val balance by viewModel.initialBalance.collectAsState()
    val colorHex by viewModel.selectedColor.collectAsState()

    val colorPalette = listOf(
        "#2196F3",
        "#4CAF50",
        "#FF9800",
        "#E91E63",
        "#9C27B0",
        "#607D8B",
        "#000000"
    )

    LaunchedEffect(uiState) {
        if (uiState is AddWalletViewModel.AddWalletUiState.Success) {
            Toast.makeText(context, "Dompet berhasil dibuat!", Toast.LENGTH_SHORT).show()
            onNavigateUp()
        }
        if (uiState is AddWalletViewModel.AddWalletUiState.Error) {
            Toast.makeText(context, (uiState as AddWalletViewModel.AddWalletUiState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Dompet", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F7FA))
            )
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text("Preview Tampilan", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            LivePreviewCard(
                name = if (name.isBlank()) "Nama Dompet" else name,
                balance = balance.toDoubleOrNull() ?: 0.0,
                type = type,
                colorHex = colorHex
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    OutlinedTextField(
                        value = name,
                        onValueChange = { viewModel.walletName.value = it },
                        label = { Text("Nama Dompet") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                        ),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = balance,
                        onValueChange = { if (it.all { c -> c.isDigit() }) viewModel.initialBalance.value = it },
                        label = { Text("Saldo Awal") },
                        prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Tipe Dompet", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        WalletTypeChip("Bank", type == "BANK") { viewModel.walletType.value = "BANK" }
                        WalletTypeChip("E-Wallet", type == "EWALLET") { viewModel.walletType.value = "EWALLET" }
                        WalletTypeChip("Cash", type == "CASH") { viewModel.walletType.value = "CASH" }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Warna Kartu", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(colorPalette) { color ->
                            ColorSwatch(
                                colorHex = color,
                                isSelected = colorHex == color,
                                onClick = { viewModel.selectedColor.value = color }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.saveWallet {} },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                elevation = ButtonDefaults.buttonElevation(8.dp),
                enabled = uiState !is AddWalletViewModel.AddWalletUiState.Loading
            ) {
                if (uiState is AddWalletViewModel.AddWalletUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Simpan Dompet", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(200.dp))
        }
    }
}


@Composable
fun LivePreviewCard(name: String, balance: Double, type: String, colorHex: String) {
    val cardColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        BrandBlue // Fallback
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = Modifier.fillMaxWidth().height(180.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(cardColor, cardColor.copy(alpha = 0.7f))
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = type,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column {
                    Text(
                        text = "Saldo Awal",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = com.trezanix.mytreza.presentation.util.formatRupiah(balance),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ColorSwatch(colorHex: String, isSelected: Boolean, onClick: () -> Unit) {
    val color = try { Color(android.graphics.Color.parseColor(colorHex)) } catch (e: Exception) { Color.Gray }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(3.dp, Color.Gray.copy(alpha = 0.5f), CircleShape)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun WalletTypeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (selected) BrandBlue else Color.White,
        contentColor = if (selected) Color.White else Color.Black,
        shape = RoundedCornerShape(12.dp),
        border = if (!selected) androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray) else null,
        modifier = Modifier
            .clickable(onClick = onClick)
            .height(40.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
        }
    }
}
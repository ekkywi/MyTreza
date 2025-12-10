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
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trezanix.mytreza.domain.model.Wallet

import com.trezanix.mytreza.presentation.theme.BrandBlue
import com.trezanix.mytreza.presentation.util.mapTypeLabel
import com.trezanix.mytreza.presentation.util.formatRupiah

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
        "#000000",
        "#795548"
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
                title = { Text("Tambah Dompet", fontWeight = FontWeight.SemiBold) },
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
            // Preview Section
            Text(
                "Preview Kartu",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LivePreviewCard(
                name = if (name.isBlank()) "Nama Dompet" else name,
                balance = balance.toDoubleOrNull() ?: 0.0,
                type = type,
                colorHex = colorHex
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form Section
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Input Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { viewModel.walletName.value = it },
                        label = { Text("Nama Dompet") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                            focusedContainerColor = Color(0xFFFAFAFA),
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        ),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )

                    // Input Balance
                    OutlinedTextField(
                        value = balance,
                        onValueChange = { if (it.all { c -> c.isDigit() }) viewModel.initialBalance.value = it },
                        label = { Text("Saldo Awal") },
                        prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                            focusedContainerColor = Color(0xFFFAFAFA),
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // Type Selector
                    Column {
                        Text(
                            "Tipe Dompet",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val types = listOf("BANK", "EWALLET", "CASH", "SAVING", "FAMILY", "ASSET")
                            items(types) { typeItem ->
                                WalletTypeChip(
                                    label = mapTypeLabel(typeItem),
                                    selected = type == typeItem,
                                    onClick = { viewModel.walletType.value = typeItem }
                                )
                            }
                        }
                    }

                    // Color Selector
                    Column {
                        Text(
                            "Warna Kartu",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
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
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { viewModel.saveWallet {} },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(16.dp, RoundedCornerShape(16.dp), spotColor = BrandBlue.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                enabled = uiState !is AddWalletViewModel.AddWalletUiState.Loading
            ) {
                if (uiState is AddWalletViewModel.AddWalletUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Simpan Dompet", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}


@Composable
fun LivePreviewCard(name: String, balance: Double, type: String, colorHex: String) {
    val cardColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        BrandBlue
    }

    val gradient = Brush.linearGradient(
        colors = listOf(cardColor, cardColor.copy(alpha = 0.8f))
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = cardColor.copy(alpha = 0.5f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
        ) {
            // Decor Circles
            Box(
                modifier = Modifier
                    .offset(x = 220.dp, y = (-30).dp)
                    .size(250.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
            )
            Box(
                modifier = Modifier
                    .offset(x = (-60).dp, y = 120.dp)
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.05f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CreditCard,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(40.dp)
                    )
                    
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = type.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }

                // Balance
                Column {
                    Text(
                        text = "Saldo Awal",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatRupiah(balance),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Footer Name
                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
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
            .size(52.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(3.dp, Color.White, CircleShape).border(5.dp, color.copy(alpha = 0.5f), CircleShape)
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
        shape = RoundedCornerShape(16.dp),
        border = if (!selected) androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)) else null,
        modifier = Modifier
            .height(44.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shadowElevation = if (selected) 8.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = label, 
                style = MaterialTheme.typography.labelLarge, 
                fontWeight = if(selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
package com.trezanix.mytreza.presentation.features.wallet.edit

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trezanix.mytreza.presentation.theme.BrandBlue
import com.trezanix.mytreza.presentation.util.formatRupiah
import com.trezanix.mytreza.presentation.util.mapTypeLabel

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import com.trezanix.mytreza.presentation.util.CategoryIcons
import com.trezanix.mytreza.presentation.util.getCategoryIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWalletScreen(
    onNavigateUp: () -> Unit,
    onArchiveSuccess: () -> Unit,
    viewModel: EditWalletViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val name by viewModel.walletName.collectAsState()
    val colorHex by viewModel.selectedColor.collectAsState()
    val balance by viewModel.currentBalance.collectAsState()
    val type by viewModel.currentType.collectAsState()
    val selectedIcon by viewModel.selectedIcon.collectAsState()

    val colorPalette = com.trezanix.mytreza.presentation.util.WalletHelper.walletColors

    var showArchiveDialog by remember { mutableStateOf(false) }
    var showIconDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is EditWalletViewModel.EditState.Success -> {
                // Success handled by viewModel callback usually, but if needed here:
            }
            is EditWalletViewModel.EditState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    if (showIconDialog) {
        AlertDialog(
            onDismissRequest = { showIconDialog = false },
            title = { Text("Pilih Ikon Dompet") },
            text = {
                Box(modifier = Modifier.height(300.dp).fillMaxWidth()) {
                    LazyVerticalGrid(columns = GridCells.Adaptive(56.dp)) {
                        items(CategoryIcons.iconKeys) { iconKey ->
                            val isSelected = selectedIcon == iconKey
                            IconButton(
                                onClick = { 
                                    viewModel.selectedIcon.value = iconKey 
                                    showIconDialog = false
                                },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .border(if(isSelected) 2.dp else 0.dp, BrandBlue, CircleShape)
                            ) {
                                Icon(
                                    imageVector = getCategoryIcon(iconKey),
                                    contentDescription = null,
                                    tint = if (isSelected) BrandBlue else Color.Gray,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showIconDialog = false }) {
                    Text("Batal")
                }
            },
            containerColor = Color.White
        )
    }

    if (showArchiveDialog) {
        AlertDialog(
            onDismissRequest = { showArchiveDialog = false },
            title = { Text("Arsipkan Dompet?") },
            text = {
                Text("Dompet ini akan disembunyikan dari daftar aktif dan tidak bisa digunakan untuk transaksi baru. Riwayat transaksi akan tetap tersimpan.\n\nPastikan saldo dompet sudah Rp 0.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showArchiveDialog = false
                        viewModel.archiveWallet {
                            Toast.makeText(context, "Dompet berhasil diarsipkan", Toast.LENGTH_SHORT).show()
                            onArchiveSuccess()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Ya, Arsipkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showArchiveDialog = false }) {
                    Text("Batal")
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Dompet", fontWeight = FontWeight.SemiBold) },
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
        if (uiState is EditWalletViewModel.EditState.Loading && name.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Preview Section
                Text("Preview Tampilan", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))

                LivePreviewCard(name, balance, type, colorHex, selectedIcon)

                Spacer(modifier = Modifier.height(32.dp))

                // Form Section
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {

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

                        // Icon Selector Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Ikon Dompet", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF5F7FA),
                                    modifier = Modifier.clickable { showIconDialog = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = getCategoryIcon(selectedIcon),
                                            contentDescription = null,
                                            tint = BrandBlue,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Ubah Ikon", color = BrandBlue, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }

                        // Color Section
                        Column {
                            Text(
                                "Warna Kartu",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(colorPalette) { color ->
                                    EditColorSwatch(
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

                // Action Buttons
                Button(
                    onClick = {
                        viewModel.updateWallet {
                            onNavigateUp()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(16.dp, RoundedCornerShape(16.dp), spotColor = BrandBlue.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    enabled = uiState !is EditWalletViewModel.EditState.Loading
                ) {
                    if (uiState is EditWalletViewModel.EditState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Simpan Perubahan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        // Gunakan toleransi (misal 1 Rupiah) untuk menghindari masalah floating point
                        if (balance > 1.0) {
                            Toast.makeText(context, "Dompet masih memiliki saldo (${formatRupiah(balance)}). Kosongkan saldo terlebih dahulu.", Toast.LENGTH_LONG).show()
                        } else {
                            showArchiveDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    enabled = uiState !is EditWalletViewModel.EditState.Loading
                ) {
                    Icon(Icons.Default.Archive, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Arsipkan / Tutup Dompet", fontWeight = FontWeight.SemiBold)
                }

                Text(
                    text = "*Dompet hanya bisa diarsipkan jika saldo Rp 0",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 12.dp, start = 4.dp),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}


@Composable
fun LivePreviewCard(name: String, balance: Double, type: String, colorHex: String, iconKey: String) {
    val cardColor = try { Color(android.graphics.Color.parseColor(colorHex)) } catch (e: Exception) { BrandBlue }

    val gradient = Brush.linearGradient(
        colors = listOf(cardColor, cardColor.copy(alpha = 0.8f))
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = cardColor.copy(alpha = 0.5f)
            ),
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
                        imageVector = getCategoryIcon(iconKey),
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(40.dp)
                    )

                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = mapTypeLabel(type).uppercase(),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Balance
                Column {
                    Text(
                        text = "Saldo Aktif", 
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
                        text = if(name.isBlank()) "Nama Dompet" else name, 
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
fun EditColorSwatch(colorHex: String, isSelected: Boolean, onClick: () -> Unit) {
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
        if (isSelected) Icon(Icons.Default.Check, null, tint = Color.White)
    }
}
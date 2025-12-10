package com.trezanix.mytreza.presentation.features.transaction.add

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trezanix.mytreza.domain.model.Category
import com.trezanix.mytreza.domain.model.Wallet
import com.trezanix.mytreza.presentation.theme.AccentGreen
import com.trezanix.mytreza.presentation.theme.AccentRed
import com.trezanix.mytreza.presentation.theme.BrandBlue
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateUp: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val amount by viewModel.amount.collectAsState()
    val note by viewModel.note.collectAsState()
    val date by viewModel.date.collectAsState()
    val transactionType by viewModel.transactionType.collectAsState()

    val selectedWalletId by viewModel.selectedWalletId.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val selectedSourceWalletId by viewModel.selectedSourceWalletId.collectAsState()
    val selectedTargetWalletId by viewModel.selectedTargetWalletId.collectAsState()

    val walletList by viewModel.wallets.collectAsState()
    val categoryList by viewModel.categories.collectAsState()

    val selectedWallet = walletList.find { it.id == selectedWalletId }
    val selectedCategory = categoryList.find { it.id == selectedCategoryId }
    val sourceWallet = walletList.find { it.id == selectedSourceWalletId }
    val targetWallet = walletList.find { it.id == selectedTargetWalletId }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddTransactionUiState.Success -> {
                Toast.makeText(context, "Transaksi Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                onNavigateUp()
                viewModel.resetState()
            }
            is AddTransactionUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    val themeColor = when (transactionType) {
        "INCOME" -> AccentGreen
        "EXPENSE" -> AccentRed
        else -> BrandBlue
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (transactionType == "TRANSFER") "Transfer" else "Transaksi Baru", fontWeight = FontWeight.SemiBold) },
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Transaction Type Selector
            TransactionTypeSelector(
                selectedType = transactionType,
                onTypeSelected = {
                    viewModel.transactionType.value = it
                    viewModel.selectedCategoryId.value = null
                }
            )

            // 2. Amount Input (Standard Keyboard)
            OutlinedTextField(
                value = amount,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        viewModel.amount.value = it
                    }
                },
                label = { Text("Jumlah (Rp)") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = themeColor),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = themeColor,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            // 3. Wallet & Category Selection
            if (transactionType == "TRANSFER") {
                AestheticDropdown(
                    label = "Dari Dompet",
                    value = sourceWallet?.name ?: "Pilih Sumber",
                    items = walletList,
                    onItemSelected = { viewModel.selectedSourceWalletId.value = it.id }
                )
                AestheticDropdown(
                    label = "Ke Dompet",
                    value = targetWallet?.name ?: "Pilih Tujuan",
                    items = walletList,
                    onItemSelected = { viewModel.selectedTargetWalletId.value = it.id }
                )

                // Admin Fee Input
                val adminFee by viewModel.adminFee.collectAsState()
                OutlinedTextField(
                    value = adminFee,
                    onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.adminFee.value = it },
                    label = { Text("Biaya Admin (Opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themeColor,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            } else {
                AestheticDropdown(
                    label = "Dompet",
                    value = selectedWallet?.name ?: "Pilih Dompet",
                    items = walletList,
                    onItemSelected = { viewModel.selectedWalletId.value = it.id }
                )

                val filteredCategories = categoryList.filter {
                    it.type == transactionType
                }

                AestheticCategoryDropdown(
                    label = "Kategori",
                    value = selectedCategory?.name ?: "Pilih Kategori",
                    items = filteredCategories,
                    onItemSelected = { viewModel.selectedCategoryId.value = it.id }
                )
            }

            // 4. Date Picker
            OutlinedTextField(
                value = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date),
                onValueChange = {},
                label = { Text("Tanggal") },
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.CalendarToday, null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = themeColor,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            // 5. Note Input
            OutlinedTextField(
                value = note,
                onValueChange = { viewModel.note.value = it },
                label = { Text("Catatan (Opsional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = themeColor,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // 6. Save Button (Bottom)
            Button(
                onClick = {
                    if (transactionType == "TRANSFER") viewModel.saveTransfer()
                    else viewModel.saveTransaction()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                enabled = uiState !is AddTransactionUiState.Loading
            ) {
                if (uiState is AddTransactionUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Check, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Transaksi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TransactionTypeSelector(selectedType: String, onTypeSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp) // Removed padding to fit full width of parent padding
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TypeTab(
            text = "Pemasukan",
            isSelected = selectedType == "INCOME",
            color = AccentGreen,
            onClick = { onTypeSelected("INCOME") },
            modifier = Modifier.weight(1f)
        )
        TypeTab(
            text = "Pengeluaran",
            isSelected = selectedType == "EXPENSE",
            color = AccentRed,
            onClick = { onTypeSelected("EXPENSE") },
            modifier = Modifier.weight(1f)
        )
        TypeTab(
            text = "Transfer",
            isSelected = selectedType == "TRANSFER",
            color = BrandBlue,
            onClick = { onTypeSelected("TRANSFER") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TypeTab(text: String, isSelected: Boolean, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) color else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun AestheticDropdown(label: String, value: String, items: List<Wallet>, onItemSelected: (Wallet) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Icon(Icons.Default.ArrowDownward, null, tint = Color.Gray)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth(0.9f).background(Color.White)) {
                items.forEach { wallet ->
                    DropdownMenuItem(text = { Text(wallet.name) }, onClick = { onItemSelected(wallet); expanded = false })
                }
            }
        }
    }
}

@Composable
fun AestheticCategoryDropdown(label: String, value: String, items: List<Category>, onItemSelected: (Category) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Icon(Icons.Default.ArrowDownward, null, tint = Color.Gray)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth(0.9f).background(Color.White)) {
                items.forEach { category ->
                    DropdownMenuItem(text = { Text(category.name) }, onClick = { onItemSelected(category); expanded = false })
                }
            }
        }
    }
}
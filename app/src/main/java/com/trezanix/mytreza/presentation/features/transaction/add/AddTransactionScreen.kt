package com.trezanix.mytreza.presentation.features.transaction.add

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trezanix.mytreza.domain.model.Wallet
import com.trezanix.mytreza.presentation.theme.BrandBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateUp: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val wallets by viewModel.wallets.collectAsState()

    // Form State
    val amount by viewModel.amount.collectAsState()
    val description by viewModel.description.collectAsState()
    val type by viewModel.transactionType.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedWallet by viewModel.selectedWallet.collectAsState()
    val sourceWallet by viewModel.sourceWallet.collectAsState()
    val targetWallet by viewModel.targetWallet.collectAsState()
    val adminFee by viewModel.adminFee.collectAsState()

    // Dropdown Logic
    var showWalletDropdown by remember { mutableStateOf(false) }
    var showSourceDropdown by remember { mutableStateOf(false) }
    var showTargetDropdown by remember { mutableStateOf(false) }

    // Dropdown Transfer Error Check
    val isTransferError = sourceWallet?.id == targetWallet?.id && sourceWallet != null

    // Date Picker Logic
    val calendar = Calendar.getInstance()
    calendar.time = selectedDate
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            calendar.set(year, month, day)
            viewModel.selectedDate.value = calendar.time
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Navigation and Error Handler
    LaunchedEffect(uiState) {
        if (uiState is AddTransactionViewModel.AddTransactionState.Success) {
            Toast.makeText(context, "Transaksi Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
            onNavigateUp()
        }
        if (uiState is AddTransactionViewModel.AddTransactionState.Error) {
            Toast.makeText(context, (uiState as AddTransactionViewModel.AddTransactionState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catat Transaksi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        // Dropdown Menu harus ditaruh di Box agar bisa melayang
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. TABS (Pengeluaran | Pemasukan | Transfer)
                item {
                    TransactionTypeSelector(
                        selectedType = type,
                        onTypeSelected = { viewModel.transactionType.value = it }
                    )
                }

                // 2. INPUT NOMINAL (Aesthetic Borderless)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Nominal", style = MaterialTheme.typography.labelMedium, color = Color.Gray)

                            val nominalColor = when(type) {
                                "EXPENSE" -> Color.Red
                                "INCOME" -> Color(0xFF4CAF50)
                                else -> BrandBlue // TRANSFER
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Rp",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = nominalColor
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                TextField(
                                    value = amount,
                                    onValueChange = {
                                        if (it.all { char -> char.isDigit() }) {
                                            viewModel.amount.value = if (it.length > 1 && it.startsWith("0")) it.substring(1) else it
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = nominalColor
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        errorContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    placeholder = { Text("0", color = Color.LightGray) }
                                )
                            }
                        }
                    }
                }

                // 3. LOGIKA FORM INPUT (Dompet, Kategori, Biaya Admin)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (type == "TRANSFER") {
                                // --- MODE TRANSFER: DI-STACK (Anti Potong Nama) ---
                                // Dompet Asal
                                // Dompet Asal
                                Box {
                                    CustomDropdownInput(
                                        label = "Dari Dompet (Asal)",
                                        selectedName = sourceWallet?.name ?: "Pilih Dompet",
                                        icon = Icons.Default.Wallet,
                                        isError = isTransferError,
                                        onClick = { showSourceDropdown = true }
                                    )

                                    // Dropdown Asal
                                    WalletDropdownMenu(
                                        wallets = wallets,
                                        selectedWallet = sourceWallet,
                                        expanded = showSourceDropdown,
                                        onDismiss = { showSourceDropdown = false },
                                        onSelected = { viewModel.sourceWallet.value = it }
                                    )
                                }

                                // Dompet Tujuan
                                // Dompet Tujuan
                                Box {
                                    CustomDropdownInput(
                                        label = "Ke Dompet (Tujuan)",
                                        selectedName = targetWallet?.name ?: "Pilih Dompet",
                                        icon = Icons.AutoMirrored.Filled.ArrowRightAlt,
                                        isError = isTransferError,
                                        onClick = { showTargetDropdown = true }
                                    )

                                    // Dropdown Tujuan
                                    WalletDropdownMenu(
                                        wallets = wallets,
                                        selectedWallet = targetWallet,
                                        expanded = showTargetDropdown,
                                        onDismiss = { showTargetDropdown = false },
                                        onSelected = { viewModel.targetWallet.value = it }
                                    )
                                }

                                // Biaya Admin (Input Custom)
                                CustomNumberInput(
                                    value = adminFee,
                                    onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.adminFee.value = it },
                                    label = "Biaya Admin (Opsional)",
                                    icon = Icons.Default.Description
                                )

                            } else {
                                // --- MODE TRANSAKSI BIASA (Income/Expense) ---
                                // Pilih Dompet
                                // Pilih Dompet
                                Box {
                                    CustomDropdownInput(
                                        label = "Pilih Dompet",
                                        selectedName = selectedWallet?.name ?: "Pilih Dompet",
                                        icon = Icons.Default.Wallet,
                                        onClick = { showWalletDropdown = true }
                                    )

                                    // Dropdown Dompet Biasa
                                    WalletDropdownMenu(
                                        wallets = wallets,
                                        selectedWallet = selectedWallet,
                                        expanded = showWalletDropdown,
                                        onDismiss = { showWalletDropdown = false },
                                        onSelected = { viewModel.selectedWallet.value = it }
                                    )
                                }

                                // Pilih Kategori (Placeholder)
                                CustomDropdownInput(
                                    label = "Kategori",
                                    selectedName = "Kategori (Segera Hadir)",
                                    icon = Icons.Default.Category,
                                    onClick = { /* Nanti buka Category Picker */ },
                                    isClickable = false // Non-aktifkan klik
                                )
                            }
                        }
                    }
                }

                // 4. INPUT TANGGAL & CATATAN (Tampilan Custom)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Tanggal
                            CustomDropdownInput(
                                label = "Tanggal",
                                selectedName = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID")).format(selectedDate),
                                icon = Icons.Default.CalendarToday,
                                onClick = { datePickerDialog.show() }
                            )

                            // Catatan (Pakai TextField borderless)
                            TextField(
                                value = description,
                                onValueChange = { viewModel.description.value = it },
                                label = { Text("Catatan", color = Color.Gray) },
                                leadingIcon = { Icon(Icons.Default.Description, null, tint = BrandBlue) },
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                textStyle = MaterialTheme.typography.bodyLarge,
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }

                // 5. TOMBOL SIMPAN
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.saveTransaction() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        enabled = uiState !is AddTransactionViewModel.AddTransactionState.Loading
                    ) {
                        if (uiState is AddTransactionViewModel.AddTransactionState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Simpan Transaksi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } // End LazyColumn Items
        } // End Box (Wrapper for Dropdown)
    } // End Scaffold
} // End AddTransactionScreen

// --- KOMPONEN BERSAMA (Custom Input & Dropdown Menu) ---

@Composable
fun TransactionTypeSelector(selectedType: String, onTypeSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf("EXPENSE" to "Pengeluaran", "INCOME" to "Pemasukan", "TRANSFER" to "Transfer").forEach { (type, label) ->
            val isSelected = selectedType == type
            val bgColor = if (isSelected) {
                when(type) {
                    "EXPENSE" -> Color.Red.copy(alpha = 0.1f)
                    "INCOME" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                    else -> BrandBlue.copy(alpha = 0.1f)
                }
            } else Color.Transparent

            val textColor = if (isSelected) {
                when(type) {
                    "EXPENSE" -> Color.Red
                    "INCOME" -> Color(0xFF4CAF50)
                    else -> BrandBlue
                }
            } else Color.Gray

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .clickable { onTypeSelected(type) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = textColor, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun CustomDropdownInput(
    label: String,
    selectedName: String,
    icon: ImageVector,
    isError: Boolean = false,
    isClickable: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().run {
            if (isClickable) clickable(onClick = onClick) else this
        }
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = if (isError) Color.Red else BrandBlue, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    selectedName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isError) Color.Red else Color.Black
                )
            }
            Icon(Icons.Default.ExpandMore, null, tint = Color.Gray)
        }
        Divider(color = if (isError) Color.Red else Color(0xFFE0E0E0), thickness = 1.dp)
        if (isError) {
            Text("Dompet Asal & Tujuan harus berbeda!", color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun CustomNumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(6.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge,
            leadingIcon = { Icon(icon, null, tint = BrandBlue) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
    }
}

@Composable
fun WalletDropdownMenu(
    wallets: List<Wallet>,
    selectedWallet: Wallet?,
    expanded: Boolean,
    onDismiss: () -> Unit,
    onSelected: (Wallet) -> Unit
) {
    if (expanded) {
        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismiss,
                modifier = Modifier
                    .background(Color.White)
                    .width(IntrinsicSize.Max) // Coba nyesuaiin lebar, atau bisa diatur fixed width
            ) {
                wallets.forEach { wallet ->
                    val isSelected = wallet.id == selectedWallet?.id
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = wallet.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) BrandBlue else Color.Black
                                )
                                Text(
                                    text = "Saldo: Rp ${wallet.balance.toInt()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        },
                        leadingIcon = {
                            // Simple logic for color tint if available, else BrandBlue
                            val walletColor = try {
                                if (!wallet.color.isNullOrEmpty()) Color(android.graphics.Color.parseColor(wallet.color)) else BrandBlue
                            } catch (e: Exception) {
                                BrandBlue
                            }
                            
                            Icon(
                                imageVector = Icons.Default.Wallet,
                                contentDescription = null,
                                tint = walletColor
                            )
                        },
                        trailingIcon = {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = BrandBlue
                                )
                            }
                        },
                        onClick = {
                            onSelected(wallet)
                            onDismiss()
                        },
                        modifier = Modifier.background(
                            if (isSelected) BrandBlue.copy(alpha = 0.05f) else Color.Transparent
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
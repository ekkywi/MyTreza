package com.trezanix.mytreza.presentation.features.transaction.add

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    val amount by viewModel.amount.collectAsState()
    val description by viewModel.description.collectAsState()
    val type by viewModel.transactionType.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedWallet by viewModel.selectedWallet.collectAsState()
    val sourceWallet by viewModel.sourceWallet.collectAsState()
    val targetWallet by viewModel.targetWallet.collectAsState()
    val adminFee by viewModel.adminFee.collectAsState()

    var showWalletDropdown by remember { mutableStateOf(false) }
    var showSourceDropdown by remember { mutableStateOf(false) }
    var showTargetDropdown by remember { mutableStateOf(false) }

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
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                TransactionTypeSelector(
                    selectedType = type,
                    onTypeSelected = { viewModel.transactionType.value = it }
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Nominal", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Rp", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = BrandBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            TextField(
                                value = amount,
                                onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.amount.value = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = { Text("0", color = Color.LightGray) }
                            )
                        }
                    }
                }
            }

            item {
                if (type == "TRANSFER") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            WalletSelector(
                                label = "Dari",
                                selectedWallet = sourceWallet,
                                wallets = wallets,
                                expanded = showSourceDropdown,
                                onExpandedChange = { showSourceDropdown = it },
                                onWalletSelected = { viewModel.sourceWallet.value = it },
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                Icons.AutoMirrored.Filled.ArrowRightAlt,
                                contentDescription = "Ke",
                                tint = Color.Gray,
                                modifier = Modifier.padding(horizontal = 8.dp).size(32.dp)
                            )

                            WalletSelector(
                                label = "Ke",
                                selectedWallet = targetWallet,
                                wallets = wallets,
                                expanded = showTargetDropdown,
                                onExpandedChange = { showTargetDropdown = it },
                                onWalletSelected = { viewModel.targetWallet.value = it },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = adminFee,
                            onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.adminFee.value = it },
                            label = { Text("Biaya Admin (Opsional)") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Text("Rp", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        WalletSelector(
                            label = "Dompet",
                            selectedWallet = selectedWallet,
                            wallets = wallets,
                            expanded = showWalletDropdown,
                            onExpandedChange = { showWalletDropdown = it },
                            onWalletSelected = { viewModel.selectedWallet.value = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = "Kategori (Segera Hadir)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategori") },
                            leadingIcon = { Icon(Icons.Default.Category, null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = false
                        )
                    }
                }
            }

            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID")).format(selectedDate),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tanggal") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                        trailingIcon = { Icon(Icons.Default.ExpandMore, null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.Black,
                            disabledBorderColor = Color.Gray,
                            disabledLabelColor = Color.Gray,
                            disabledLeadingIconColor = Color.Gray,
                            disabledTrailingIconColor = Color.Gray
                        )
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { datePickerDialog.show() }
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { viewModel.description.value = it },
                    label = { Text("Catatan") },
                    leadingIcon = { Icon(Icons.Default.Description, null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        viewModel.saveTransaction()
                    },
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
        }
    }
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletSelector(
    label: String,
    selectedWallet: Wallet?,
    wallets: List<Wallet>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onWalletSelected: (Wallet) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedWallet?.name ?: "Pilih Dompet",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(Icons.Default.Wallet, null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandBlue,
                unfocusedBorderColor = Color.Gray
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            wallets.forEach { wallet ->
                DropdownMenuItem(
                    text = { Text(wallet.name) },
                    onClick = {
                        onWalletSelected(wallet)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}
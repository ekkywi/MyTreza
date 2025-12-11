package com.trezanix.mytreza.presentation.features.transaction.add

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trezanix.mytreza.R
import com.trezanix.mytreza.presentation.components.AestheticCategoryDropdown
import com.trezanix.mytreza.presentation.components.AestheticDatePicker
import com.trezanix.mytreza.presentation.components.AestheticTimePicker
import com.trezanix.mytreza.presentation.components.AestheticWalletDropdown
import com.trezanix.mytreza.presentation.theme.AccentGreen
import com.trezanix.mytreza.presentation.theme.AccentRed
import com.trezanix.mytreza.presentation.theme.BrandBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormScreen(
    navController: NavController,
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
    val isEditMode by viewModel.isEditMode.collectAsState()
    val adminFee by viewModel.adminFee.collectAsState()

    val walletList by viewModel.wallets.collectAsState()
    val categoryList by viewModel.categories.collectAsState()

    val selectedWallet = walletList.find { it.id == selectedWalletId }
    val selectedCategory = categoryList.find { it.id == selectedCategoryId }
    val sourceWallet = walletList.find { it.id == selectedSourceWalletId }
    val targetWallet = walletList.find { it.id == selectedTargetWalletId }

    val calendar = remember { Calendar.getInstance() }
    
    LaunchedEffect(date) {
        calendar.time = date
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        AestheticDatePicker(
            date = date,
            onDismiss = { showDatePicker = false },
            onConfirm = {
                calendar.timeInMillis = it
                showDatePicker = false
                showTimePicker = true
            }
        )
    }

    if (showTimePicker) {
        AestheticTimePicker(
            date = calendar.time,
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                viewModel.date.value = calendar.time
                showTimePicker = false
            }
        )
    }

    // Resources for Toast
    val msgSuccessUpdate = stringResource(R.string.msg_success_update)
    val msgSuccessSave = stringResource(R.string.msg_success_save)

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddTransactionUiState.Success -> {
                Toast.makeText(context, if (isEditMode) msgSuccessUpdate else msgSuccessSave, Toast.LENGTH_SHORT).show()
                navController.popBackStack()
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
                title = { 
                    Text(
                        if (isEditMode) stringResource(R.string.title_edit_transaction) 
                        else if (transactionType == "TRANSFER") stringResource(R.string.title_transfer) 
                        else stringResource(R.string.title_add_transaction), 
                        fontWeight = FontWeight.SemiBold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.nav_back))
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
                enabled = !isEditMode,
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
                label = { Text(stringResource(R.string.label_amount)) },
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
                AestheticWalletDropdown(
                    label = stringResource(R.string.label_source_wallet),
                    value = sourceWallet?.name ?: stringResource(R.string.placeholder_select_source),
                    items = walletList,
                    enabled = !isEditMode,
                    onItemSelected = { viewModel.selectedSourceWalletId.value = it.id }
                )
                 // NOTE: AestheticCategoryDropdown is used below, but here we need WalletDropdown for target too
                AestheticWalletDropdown(
                    label = stringResource(R.string.label_target_wallet),
                    value = targetWallet?.name ?: stringResource(R.string.placeholder_select_target),
                    items = walletList,
                    enabled = !isEditMode,
                    onItemSelected = { viewModel.selectedTargetWalletId.value = it.id }
                )

                // Admin Fee Input
                OutlinedTextField(
                    value = adminFee,
                    onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.adminFee.value = it },
                    label = { Text(stringResource(R.string.label_admin_fee)) },
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
                AestheticWalletDropdown(
                    label = stringResource(R.string.label_wallet),
                    value = selectedWallet?.name ?: stringResource(R.string.placeholder_select_wallet),
                    items = walletList,
                    enabled = !isEditMode,
                    onItemSelected = { viewModel.selectedWalletId.value = it.id }
                )

                val filteredCategories = categoryList.filter {
                    it.type == transactionType
                }

                AestheticCategoryDropdown(
                    label = stringResource(R.string.label_category),
                    value = selectedCategory?.name ?: stringResource(R.string.placeholder_select_category),
                    items = filteredCategories,
                    onItemSelected = { viewModel.selectedCategoryId.value = it.id }
                )
            }

            // 4. Date Picker
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(date),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.label_date)) },
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
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showDatePicker = true }
                )
            }

            // 5. Note Input
            OutlinedTextField(
                value = note,
                onValueChange = { viewModel.note.value = it },
                label = { Text(stringResource(R.string.label_note)) },
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
                    Text(if (isEditMode) stringResource(R.string.btn_update_transaction) else stringResource(R.string.btn_save_transaction), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TransactionTypeSelector(selectedType: String, enabled: Boolean = true, onTypeSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TypeTab(
            text = stringResource(R.string.label_income),
            isSelected = selectedType == "INCOME",
            color = AccentGreen,
            enabled = enabled,
            onClick = { onTypeSelected("INCOME") },
            modifier = Modifier.weight(1f)
        )
        TypeTab(
            text = stringResource(R.string.label_expense),
            isSelected = selectedType == "EXPENSE",
            color = AccentRed,
            enabled = enabled,
            onClick = { onTypeSelected("EXPENSE") },
            modifier = Modifier.weight(1f)
        )
        TypeTab(
            text = stringResource(R.string.label_transfer),
            isSelected = selectedType == "TRANSFER",
            color = BrandBlue,
            enabled = enabled,
            onClick = { onTypeSelected("TRANSFER") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TypeTab(text: String, isSelected: Boolean, color: Color, enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val alpha = if (enabled || isSelected) 1f else 0.5f
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) color else Color.Gray.copy(alpha = alpha),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

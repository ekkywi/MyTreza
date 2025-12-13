package com.trezanix.mytreza.presentation.features.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trezanix.mytreza.domain.model.Transaction
import com.trezanix.mytreza.presentation.components.TransactionItem
import com.trezanix.mytreza.presentation.features.dashboard.toDomain
import com.trezanix.mytreza.presentation.theme.BrandBlue
import com.trezanix.mytreza.presentation.util.formatDateHeader

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    onNavigateUp: () -> Unit,
    onNavigateToDetail: (Transaction) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val transactions by viewModel.displayedTransactions.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterType by viewModel.filterType.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.loadTransactions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Local state for interaction
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Group transactions by date header
    val groupedTransactions = remember(transactions) {
        transactions.groupBy { formatDateHeader(it.date) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Transaksi", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8F9FA))
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        // Main Content
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 1. HEADER SECTION (Search + Filter)
            Column(
                modifier = Modifier
                    .background(Color(0xFFF8F9FA))
                    .padding(bottom = 8.dp)
            ) {
                SearchBarModern(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange
                )

                FilterSectionModern(
                    selectedFilter = filterType,
                    onFilterSelected = viewModel::onFilterChange
                )
            }

            // 2. LIST TRANSAKSI
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandBlue)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 20.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (groupedTransactions.isEmpty()) {
                        item {
                            EmptyHistoryState(isSearching = searchQuery.isNotEmpty())
                        }
                    } else {
                        groupedTransactions.forEach { (dateHeader, transactionList) ->
                            stickyHeader {
                                DateHeader(dateHeader)
                            }
                            
                            items(transactionList) { trxDto ->
                                // Custom Item Wrapper for Cleaner Look
                                Box(
                                    modifier = Modifier
                                        .clickable { 
                                            // Set selected transaction to show bottom sheet
                                            selectedTransaction = trxDto.toDomain() 
                                        }
                                ) {
                                    // Modified Transaction Item (Reusable Component)
                                    // Note: We might want a flatter version here, but standard one is fine for now
                                    TransactionItem(transaction = trxDto)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- BOTTOM SHEET DETAIL ---
        if (selectedTransaction != null) {
            com.trezanix.mytreza.presentation.features.transaction.detail.TransactionDetailSheet(
                transaction = selectedTransaction!!,
                onDismiss = { selectedTransaction = null },
                onEdit = {
                    selectedTransaction?.let { trx ->
                        onNavigateToDetail(trx)
                    }
                    selectedTransaction = null
                },
                onDelete = {
                    showDeleteDialog = true
                    // Don't dismiss sheet yet
                }
            )
        }

        // --- DELETE CONFIRMATION DIALOG ---
        if (showDeleteDialog && selectedTransaction != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Hapus Transaksi?") },
                text = { Text("Data yang dihapus tidak dapat dikembalikan. Lanjutkan?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedTransaction?.id?.let { id ->
                                viewModel.deleteTransaction(id)
                            }
                            showDeleteDialog = false
                            selectedTransaction = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = com.trezanix.mytreza.presentation.theme.AccentRed)
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
}

@Composable
fun DateHeader(title: String) {
    Surface(
        color = Color(0xFFF8F9FA), // Match background to blend in, or slightly transparent
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )
    }
}

@Composable
fun SearchBarModern(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black
                ),
                cursorBrush = SolidColor(BrandBlue),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "Cari transaksi...",
                            style = TextStyle(fontSize = 16.sp, color = Color.Gray)
                        )
                    }
                    innerTextField()
                }
            )

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterSectionModern(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            FilterChipModern("Semua", "ALL", selectedFilter, onFilterSelected)
        }
        item {
            FilterChipModern("Pemasukan", "INCOME", selectedFilter, onFilterSelected)
        }
        item {
            FilterChipModern("Pengeluaran", "EXPENSE", selectedFilter, onFilterSelected)
        }
    }
}

@Composable
fun FilterChipModern(
    label: String,
    type: String,
    selectedType: String,
    onSelect: (String) -> Unit
) {
    val isSelected = type == selectedType
    val backgroundColor = if (isSelected) BrandBlue else Color.White
    val contentColor = if (isSelected) Color.White else Color.Gray
    val borderColor = if (isSelected) BrandBlue else Color(0xFFE0E0E0)

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .clickable { onSelect(type) }
            .height(36.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

@Composable
fun EmptyHistoryState(isSearching: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSearching) Icons.Default.Search else Icons.Default.FilterList,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isSearching) "Transaksi tidak ditemukan" else "Belum ada riwayat transaksi",
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}
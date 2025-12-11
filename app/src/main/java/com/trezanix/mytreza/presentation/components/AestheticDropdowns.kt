package com.trezanix.mytreza.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trezanix.mytreza.domain.model.Category
import com.trezanix.mytreza.domain.model.Wallet
import com.trezanix.mytreza.presentation.theme.AccentGreen
import com.trezanix.mytreza.presentation.theme.AccentRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AestheticWalletDropdown(
    label: String,
    value: String,
    items: List<Wallet>,
    onItemSelected: (Wallet) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Companion.Gray,
            modifier = Modifier.Companion.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Companion.White)
                .border(
                    1.dp,
                    Color.Companion.LightGray.copy(alpha = 0.5f),
                    androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                )
                .clickable { showSheet = true }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Companion.CenterStart
        ) {
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Companion.Medium
                )
                Icon(Icons.Default.ArrowDownward, null, tint = Color.Companion.Gray)
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color.Companion.White
        ) {
            Column(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 48.dp)
            ) {
                Text(
                    text = "Pilih Dompet",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Companion.Bold,
                    modifier = Modifier.Companion.padding(bottom = 16.dp)
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(items) { wallet ->
                        Row(
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                                .background(Color(0xFFF8F9FA))
                                .clickable {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showSheet = false
                                            onItemSelected(wallet)
                                        }
                                    }
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.Companion.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                                Box(
                                    modifier = Modifier.Companion
                                        .size(40.dp)
                                        .background(
                                            Color.Companion.LightGray.copy(alpha = 0.2f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Companion.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.Companion.width(16.dp))
                                Column {
                                    Text(
                                        text = wallet.name,
                                        fontWeight = FontWeight.Companion.Bold,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = wallet.type, // e.g. "Cash", "Bank"
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Companion.Gray
                                    )
                                }
                            }

                            Text(
                                text = formatCurrency(wallet.balance),
                                color = if (wallet.balance >= 0) AccentGreen else AccentRed,
                                fontWeight = FontWeight.Companion.SemiBold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AestheticCategoryDropdown(
    label: String,
    value: String,
    items: List<Category>,
    onItemSelected: (com.trezanix.mytreza.domain.model.Category) -> Unit
) {
    var showSheet by _root_ide_package_.androidx.compose.runtime.remember {
        _root_ide_package_.androidx.compose.runtime.mutableStateOf(
            false
        )
    }
    val sheetState = _root_ide_package_.androidx.compose.material3.rememberModalBottomSheetState()
    val scope = _root_ide_package_.androidx.compose.runtime.rememberCoroutineScope()

    _root_ide_package_.androidx.compose.foundation.layout.Column {
        _root_ide_package_.androidx.compose.material3.Text(
            text = label,
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
            color = androidx.compose.ui.graphics.Color.Companion.Gray,
            modifier = androidx.compose.ui.Modifier.Companion.padding(bottom = 8.dp)
        )
        _root_ide_package_.androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier.Companion
                .fillMaxWidth()
                .height(56.dp)
                .clip(_root_ide_package_.androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .background(androidx.compose.ui.graphics.Color.Companion.White)
                .border(
                    1.dp,
                    androidx.compose.ui.graphics.Color.Companion.LightGray.copy(alpha = 0.5f),
                    _root_ide_package_.androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                )
                .clickable { showSheet = true }
                .padding(horizontal = 16.dp),
            contentAlignment = androidx.compose.ui.Alignment.Companion.CenterStart
        ) {
            _root_ide_package_.androidx.compose.foundation.layout.Row(
                modifier = androidx.compose.ui.Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.Companion.CenterVertically
            ) {
                _root_ide_package_.androidx.compose.material3.Text(
                    text = value,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Companion.Medium
                )
                _root_ide_package_.androidx.compose.material3.Icon(
                    androidx.compose.material.icons.Icons.Default.ArrowDownward,
                    null,
                    tint = androidx.compose.ui.graphics.Color.Companion.Gray
                )
            }
        }
    }

    if (showSheet) {
        _root_ide_package_.androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = androidx.compose.ui.graphics.Color.Companion.White
        ) {
            _root_ide_package_.androidx.compose.foundation.layout.Column(
                modifier = androidx.compose.ui.Modifier.Companion
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 48.dp)
            ) {
                _root_ide_package_.androidx.compose.material3.Text(
                    text = "Pilih Kategori",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Companion.Bold,
                    modifier = androidx.compose.ui.Modifier.Companion.padding(bottom = 16.dp)
                )

                _root_ide_package_.androidx.compose.foundation.lazy.LazyColumn(
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                        12.dp
                    )
                ) {
                    items(items) { category ->
                        _root_ide_package_.androidx.compose.foundation.layout.Row(
                            modifier = androidx.compose.ui.Modifier.Companion
                                .fillMaxWidth()
                                .clip(
                                    _root_ide_package_.androidx.compose.foundation.shape.RoundedCornerShape(
                                        16.dp
                                    )
                                )
                                .background(
                                    _root_ide_package_.androidx.compose.ui.graphics.Color(
                                        0xFFF8F9FA
                                    )
                                )
                                .clickable {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showSheet = false
                                            onItemSelected(category)
                                        }
                                    }
                                }
                                .padding(16.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.Companion.CenterVertically
                        ) {
                            _root_ide_package_.androidx.compose.foundation.layout.Box(
                                modifier = androidx.compose.ui.Modifier.Companion
                                    .size(40.dp)
                                    .background(
                                        androidx.compose.ui.graphics.Color.Companion.LightGray.copy(
                                            alpha = 0.2f
                                        ),
                                        _root_ide_package_.androidx.compose.foundation.shape.CircleShape
                                    ),
                                contentAlignment = androidx.compose.ui.Alignment.Companion.Center
                            ) {
                                _root_ide_package_.androidx.compose.material3.Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Category,
                                    contentDescription = null,
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
                                )
                            }
                            _root_ide_package_.androidx.compose.foundation.layout.Spacer(
                                modifier = androidx.compose.ui.Modifier.Companion.width(
                                    16.dp
                                )
                            )
                            _root_ide_package_.androidx.compose.material3.Text(
                                text = category.name,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Companion.Bold,
                                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val format = java.text.NumberFormat.getCurrencyInstance(
        _root_ide_package_.java.util.Locale(
            "id",
            "ID"
        )
    )
    return format.format(amount).replace("Rp", "Rp ").replace(",00", "")
}
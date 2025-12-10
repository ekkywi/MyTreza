package com.trezanix.mytreza.presentation.features.transaction.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trezanix.mytreza.domain.model.Transaction
import com.trezanix.mytreza.presentation.theme.AccentGreen
import com.trezanix.mytreza.presentation.theme.AccentRed
import com.trezanix.mytreza.presentation.theme.BrandBlue
import com.trezanix.mytreza.presentation.util.formatRupiah
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailSheet(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header Besar (Nominal)
            val isExpense = transaction.type == "EXPENSE"
            val color = if (isExpense) AccentRed else AccentGreen
            val prefix = if (isExpense) "- " else "+ "

            Text(
                text = "Detail Transaksi",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$prefix${formatRupiah(transaction.amount)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Info Cards
            DetailRow(icon = Icons.Outlined.Category, label = "Kategori", value = transaction.categoryName)
            DetailRow(icon = Icons.Outlined.AccountBalanceWallet, label = "Dompet", value = transaction.walletName)

            // Format Tanggal Cantik
            val formattedDate = try {
                val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
                val dateObj = parser.parse(transaction.date)
                formatter.format(dateObj!!)
            } catch (e: Exception) {
                transaction.date
            }
            DetailRow(icon = Icons.Outlined.Today, label = "Tanggal", value = formattedDate)

            if (!transaction.description.isNullOrEmpty()) {
                DetailRow(icon = Icons.Outlined.Description, label = "Catatan", value = transaction.description)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Action Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Tombol Hapus
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentRed.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Delete, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hapus")
                }

                // Tombol Edit
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Icon(Icons.Default.Edit, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit")
                }
            }
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFF5F7FA), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = BrandBlue, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}
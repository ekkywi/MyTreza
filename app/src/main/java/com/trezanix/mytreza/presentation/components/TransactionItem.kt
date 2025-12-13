package com.trezanix.mytreza.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trezanix.mytreza.data.remote.dto.TransactionDto
import com.trezanix.mytreza.presentation.theme.AccentGreen
import com.trezanix.mytreza.presentation.theme.AccentRed
import com.trezanix.mytreza.presentation.util.formatRupiah
import com.trezanix.mytreza.presentation.util.getCategoryIcon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionItem(transaction: TransactionDto) {
    val isExpense = transaction.type == "EXPENSE"
    val amountColor = if (isExpense) AccentRed else AccentGreen
    val iconBgColor = if (isExpense) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
    val icon = if (isExpense) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
    val prefix = if (isExpense) "- " else "+ "

    val dateReadable = try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val formatter = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))
        val parsed = parser.parse(transaction.date) ?: Date()
        formatter.format(parsed)
    } catch (e: Exception) { "-" }

    val walletName = transaction.wallet?.name ?: "Dompet"
    val isTransfer = transaction.category?.name?.contains("Transfer", true) == true

    // Dynamic Icon Logic
    val categoryIconName = transaction.category?.icon
    val categoryColorHex = transaction.category?.color

    val databaseIcon = if (categoryIconName != null) getCategoryIcon(categoryIconName) else null
    val displayIcon = databaseIcon ?: (if (isTransfer) Icons.Default.SwapHoriz else icon)

    // Dynamic Color Logic
    val displayColor = try {
        if (categoryColorHex != null) Color(android.graphics.Color.parseColor(categoryColorHex)) else amountColor
    } catch (e: Exception) { amountColor }

    val displayBgColor = if (categoryColorHex != null) displayColor.copy(alpha = 0.1f) else iconBgColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(displayBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(displayIcon, null, tint = displayColor, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                transaction.category?.name ?: transaction.description ?: "Transaksi",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "$walletName • $dateReadable",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Text(
            "$prefix${formatRupiah(transaction.amount)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}
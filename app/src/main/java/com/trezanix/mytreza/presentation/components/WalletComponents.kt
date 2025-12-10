package com.trezanix.mytreza.presentation.components

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trezanix.mytreza.domain.model.Wallet
import com.trezanix.mytreza.presentation.theme.BrandBlue
import com.trezanix.mytreza.presentation.util.formatRupiah

@Composable
fun WalletCard(wallet: Wallet, onClick: (() -> Unit)? = null) {
    val defaultColor = getDefaultColorByType(wallet.type)
    val baseColor = if (!wallet.color.isNullOrBlank()) {
        try {
            androidx.compose.ui.graphics.Color(Color.parseColor(wallet.color))
        } catch (e: Exception) {
            defaultColor
        }
    } else {
        defaultColor
    }

    val gradient = Brush.Companion.linearGradient(
        colors = listOf(baseColor, baseColor.copy(alpha = 0.8f))
    )

    val icon = when (wallet.type.uppercase()) {
        "CASH" -> Icons.Rounded.AttachMoney
        "EWALLET" -> Icons.Rounded.AccountBalanceWallet
        "BANK" -> Icons.Rounded.AccountBalance
        "SAVING" -> Icons.Rounded.Star
        "FAMILY" -> Icons.Rounded.Group
        "ASSET" -> Icons.Rounded.Apartment
        else -> Icons.Rounded.AccountBalance
    }

    val modifier = Modifier.Companion
        .fillMaxWidth()
        .height(180.dp)
        .shadow(
            elevation = 12.dp,
            shape = RoundedCornerShape(24.dp),
            spotColor = baseColor.copy(alpha = 0.4f),
            ambientColor = baseColor.copy(alpha = 0.2f)
        )

    val finalModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Card(
        modifier = finalModifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Companion.Transparent)
    ) {
        Box(
            modifier = Modifier.Companion
                .fillMaxSize()
                .background(gradient)
        ) {
            // Hiasan Circle Background Transparan
            Box(
                modifier = Modifier.Companion
                    .offset(x = 200.dp, y = (-20).dp)
                    .size(250.dp)
                    .clip(CircleShape)
                    .background(androidx.compose.ui.graphics.Color.Companion.White.copy(alpha = 0.05f))
            )
            Box(
                modifier = Modifier.Companion
                    .offset(x = (-50).dp, y = 100.dp)
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(androidx.compose.ui.graphics.Color.Companion.Black.copy(alpha = 0.05f))
            )

            Column(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Chip & Type
                Row(
                    modifier = Modifier.Companion.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Companion.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CreditCard, // Simbol Chip
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color.Companion.White.copy(alpha = 0.8f),
                        modifier = Modifier.Companion.size(36.dp)
                    )

                    Surface(
                        color = androidx.compose.ui.graphics.Color.Companion.White.copy(alpha = 0.2f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                        modifier = Modifier.Companion.padding(start = 8.dp)
                    ) {
                        Text(
                            text = wallet.type.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Companion.Bold,
                            color = androidx.compose.ui.graphics.Color.Companion.White,
                            modifier = Modifier.Companion.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            )
                        )
                    }
                }

                // Middle: Account Number Placeholder
                Text(
                    text = "**** **** **** ${wallet.id.takeLast(4)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = androidx.compose.ui.graphics.Color.Companion.White.copy(alpha = 0.6f),
                    letterSpacing = 3.sp,
                    modifier = Modifier.Companion.padding(vertical = 8.dp)
                )

                // Bottom Row: Name & Balance
                Row(
                    modifier = Modifier.Companion.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Companion.Bottom
                ) {
                    Column {
                        Text(
                            text = "Pemilik",
                            style = MaterialTheme.typography.labelSmall,
                            color = androidx.compose.ui.graphics.Color.Companion.White.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.Companion.height(2.dp))
                        Text(
                            text = wallet.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Companion.SemiBold,
                            color = androidx.compose.ui.graphics.Color.Companion.White
                        )
                    }

                    Column(horizontalAlignment = Alignment.Companion.End) {
                        Text(
                            text = "Saldo",
                            style = MaterialTheme.typography.labelSmall,
                            color = androidx.compose.ui.graphics.Color.Companion.White.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.Companion.height(2.dp))
                        Text(
                            text = formatRupiah(wallet.balance),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Companion.Bold,
                            color = androidx.compose.ui.graphics.Color.Companion.White
                        )
                    }
                }
            }
        }
    }
}

fun getDefaultColorByType(type: String): androidx.compose.ui.graphics.Color {
    return when (type.uppercase()) {
        "CASH" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
        "EWALLET" -> androidx.compose.ui.graphics.Color(0xFFFFA726)
        "BANK" -> BrandBlue
        "SAVING" -> androidx.compose.ui.graphics.Color(0xFF9C27B0)
        "FAMILY" -> androidx.compose.ui.graphics.Color(0xFFE91E63)
        "ASSET" -> androidx.compose.ui.graphics.Color(0xFF607D8B)
        else -> BrandBlue
    }
}
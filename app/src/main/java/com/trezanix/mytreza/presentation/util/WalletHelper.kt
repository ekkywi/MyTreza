package com.trezanix.mytreza.presentation.util

import androidx.compose.ui.graphics.Color

object WalletHelper {

    // Premium / Debit Card Style Colors
    val walletColors = listOf(
        "#1A237E", // Midnight Blue - Classic, trustworthy bank look
        "#1B5E20", // Emerald Green - Wealth, stability
        "#880E4F", // Burgundy - Premium, exclusive
        "#212121", // Charcoal / Black - Modern, infinite card style
        "#4A148C", // Deep Purple - Royal, high tier
        "#00695C", // Teal - Balanced, calm growth
        "#5D4037", // Bronze / Brown - Earthy, solid
        "#455A64", // Slate Grey - Corporate, neutral
        "#0D47A1", // Royal Blue - Richer blue
        "#B71C1C", // Crimson - Alert, debt or high priority
        "#F57F17", // Dark Gold - Precious
        "#006064", // Cyan Dark - Tech/Digital oriented
        "#3E2723", // Dark Brown - Leather wallet feel
        "#263238", // Blue Grey - Modern tech bank
        "#BF360C", // Deep Orange - High contrast
        "#33691E", // Light Green - Fresh growth
        "#311B92", // Indigo - Deep tech
        "#827717", // Lime Dark - Unique
        "#1E88E5", // Ocean Blue - Standard debit
        "#607D8B"  // Blue Grey Light - Silver card look
    )

    fun getWalletColor(hex: String?): Color {
        return try {
            Color(android.graphics.Color.parseColor(hex ?: "#2196F3"))
        } catch (e: Exception) {
            Color(0xFF2196F3) 
        }
    }
}

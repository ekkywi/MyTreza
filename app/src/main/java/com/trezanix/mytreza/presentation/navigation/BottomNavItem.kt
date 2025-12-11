package com.trezanix.mytreza.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Beranda")
    object Analysis : BottomNavItem("analysis", Icons.Default.History, "Analisis")
    object Wallet : BottomNavItem("wallet", Icons.Default.Wallet, "Dompet")
    object Profile : BottomNavItem("profile", Icons.Default.AccountCircle, "Profil")
}
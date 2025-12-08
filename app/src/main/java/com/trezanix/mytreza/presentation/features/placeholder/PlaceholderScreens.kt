package com.trezanix.mytreza.presentation.features.placeholder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HistoryScreen() { ScreenContent("Riwayat Transaksi") }

@Composable
fun WalletScreen() { ScreenContent("Daftar Dompet & Akun") }

@Composable
fun ProfileScreen() { ScreenContent("Profil Pengguna") }

@Composable
fun ScreenContent(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}
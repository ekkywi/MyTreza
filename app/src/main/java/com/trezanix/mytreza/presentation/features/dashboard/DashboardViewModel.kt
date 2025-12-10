package com.trezanix.mytreza.presentation.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trezanix.mytreza.data.remote.dto.TransactionDto
import com.trezanix.mytreza.domain.repository.UserRepository
import com.trezanix.mytreza.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val walletRepository: WalletRepository, // Mengurus Uang (Saldo & Transaksi)
    private val userRepository: UserRepository      // Mengurus Orang (Profil & Nama)
) : ViewModel() {

    // --- STATES ---

    // 1. Saldo Total
    private val _totalBalance = MutableStateFlow(0.0)
    val totalBalance = _totalBalance.asStateFlow()

    // 2. List Transaksi Terbaru
    private val _recentTransactions = MutableStateFlow<List<TransactionDto>>(emptyList())
    val recentTransactions = _recentTransactions.asStateFlow()

    // 3. Status Loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // 4. Pesan Notifikasi (Toast)
    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    // 5. Nama User (Default sementara sebelum load)
    private val _userName = MutableStateFlow("Memuat...")
    val userName = _userName.asStateFlow()

    // --- INITIALIZATION ---
    init {
        loadData()
    }

    // --- MAIN FUNCTIONS ---

    fun loadData() {
        viewModelScope.launch {
            // Tampilkan loading hanya jika data masih kosong (biar tidak flickering saat auto-refresh)
            if (_recentTransactions.value.isEmpty()) {
                _isLoading.value = true
            }

            // 1. Ambil Profil User (Nama)
            userRepository.getUserProfile()
                .onSuccess { user ->
                    _userName.value = user.fullName ?: "Kawan MyTreza"
                }
                .onFailure {
                    _userName.value = "Kawan MyTreza"
                }
            // 2. Ambil Total Saldo (Sum of Wallets)
            walletRepository.getWallets()
                .onSuccess { wallets ->
                    _totalBalance.value = wallets.sumOf { it.balance }
                }
                .onFailure {
                    // Silent fail untuk saldo, user akan sadar jika saldo 0 atau error toast lain
                }

            // 3. Ambil 5 Transaksi Terakhir
            walletRepository.getTransactions(page = 1, limit = 5)
                .onSuccess { transactions ->
                    _recentTransactions.value = transactions
                }
                .onFailure {
                    // Handle error transaksi
                }

            _isLoading.value = false
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            _isLoading.value = true // Tampilkan loading sebentar saat menghapus

            walletRepository.deleteTransaction(id)
                .onSuccess {
                    _message.value = "Transaksi berhasil dihapus"
                    loadData() // [PENTING] Refresh data agar transaksi hilang & saldo terupdate otomatis
                }
                .onFailure { e ->
                    _message.value = "Gagal menghapus: ${e.message}"
                    _isLoading.value = false // Stop loading jika gagal (jika sukses, stop di akhir loadData)
                }
        }
    }

    // Fungsi untuk mereset pesan setelah ditampilkan di Toast
    fun clearMessage() {
        _message.value = null
    }
}
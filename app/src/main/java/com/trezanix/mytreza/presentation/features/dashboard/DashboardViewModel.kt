package com.trezanix.mytreza.presentation.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trezanix.mytreza.data.remote.dto.TransactionDto
import com.trezanix.mytreza.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {

    // State untuk UI
    private val _totalBalance = MutableStateFlow(0.0)
    val totalBalance = _totalBalance.asStateFlow()

    private val _recentTransactions = MutableStateFlow<List<TransactionDto>>(emptyList())
    val recentTransactions = _recentTransactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getWallets()
                .onSuccess { wallets ->
                    _totalBalance.value = wallets.sumOf { it.balance }
                }
                .onFailure {
                    _error.value = "Gagal memuat saldo"
                }

            repository.getTransactions(page = 1, limit = 5)
                .onSuccess { transactions ->
                    _recentTransactions.value = transactions
                }
                .onFailure {
                }

            _isLoading.value = false
        }
    }
}
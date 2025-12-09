package com.trezanix.mytreza.presentation.features.wallet.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trezanix.mytreza.data.remote.dto.DailyStatsDto
import com.trezanix.mytreza.data.remote.dto.TransactionDto
import com.trezanix.mytreza.domain.model.WalletStats
import com.trezanix.mytreza.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class WalletDetailViewModel @Inject constructor(
    private val repository: WalletRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val walletId: String = checkNotNull(savedStateHandle["walletId"])

    private val calendar = Calendar.getInstance()
    private val _selectedMonth = MutableStateFlow(calendar.get(Calendar.MONTH) + 1)
    private val _selectedYear = MutableStateFlow(calendar.get(Calendar.YEAR))

    val selectedMonth = _selectedMonth.asStateFlow()
    val selectedYear = _selectedYear.asStateFlow()

    private val _state = MutableStateFlow<DetailState>(DetailState.Loading)
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    fun changeMonth(month: Int, year: Int) {
        _selectedMonth.value = month
        _selectedYear.value = year
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = DetailState.Loading

            val m = _selectedMonth.value
            val y = _selectedYear.value

            val walletResult = repository.getWalletDetail(walletId)

            if (walletResult.isSuccess) {
                val wallet = walletResult.getOrNull()!!
                val trxResult = repository.getWalletTransactions(walletId, m, y)
                val transactions = trxResult.getOrDefault(emptyList())
                val statsResult = repository.getWalletStats(walletId, m, y)
                val stats = statsResult.getOrDefault(WalletStats(0.0, 0.0, 0.0))
                val dailyResult = repository.getWalletDailyStats(walletId, m, y)
                val dailyStats = dailyResult.getOrDefault(emptyList())

                _state.value = DetailState.Success(wallet, transactions, stats, dailyStats)
            } else {
                _state.value = DetailState.Error(walletResult.exceptionOrNull()?.message ?: "Gagal memuat")
            }
        }
    }

    fun deleteWallet(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = DetailState.Loading

            repository.deleteWallet(walletId)
                .onSuccess {
                    onSuccess()
                }
                .onFailure { e ->
                    loadData()
                }
        }
    }

    sealed class DetailState {
        object Loading : DetailState()
        data class Success(
            val wallet: com.trezanix.mytreza.domain.model.Wallet,
            val transactions: List<TransactionDto>,
            val stats: WalletStats,
            val dailyStats: List<DailyStatsDto>
        ) : DetailState()
        data class Error(val message: String) : DetailState()
    }
}
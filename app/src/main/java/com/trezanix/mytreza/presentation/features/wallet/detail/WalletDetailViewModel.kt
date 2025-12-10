package com.trezanix.mytreza.presentation.features.wallet.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trezanix.mytreza.domain.model.Transaction // Import Domain
import com.trezanix.mytreza.domain.model.Wallet     // Import Domain
import com.trezanix.mytreza.domain.model.WalletStats // Import Domain
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

    // --- STATE MENGGUNAKAN DOMAIN MODEL (BUKAN DTO) ---
    private val _wallet = MutableStateFlow<Wallet?>(null)
    val wallet = _wallet.asStateFlow()

    private val _stats = MutableStateFlow<WalletStats?>(null)
    val stats = _stats.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions = _transactions.asStateFlow()

    private val _selectedDate = MutableStateFlow(Calendar.getInstance())
    val selectedDate = _selectedDate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true

            val calendar = _selectedDate.value
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)

            // 1. Get Detail (Repository return Result<Wallet>)
            repository.getWalletDetail(walletId)
                .onSuccess { _wallet.value = it }

            // 2. Get Stats (Repository return Result<WalletStats>)
            repository.getWalletStats(walletId, month, year)
                .onSuccess { _stats.value = it }
                .onFailure { /* Handle error */ }

            // 3. Get Transactions (Repository return Result<List<Transaction>>)
            repository.getTransactionsByWallet(walletId, month, year)
                .onSuccess { _transactions.value = it }
                .onFailure { _transactions.value = emptyList() }

            _isLoading.value = false
        }
    }

    fun nextMonth() {
        val newCal = _selectedDate.value.clone() as Calendar
        newCal.add(Calendar.MONTH, 1)
        _selectedDate.value = newCal
        loadData()
    }

    fun prevMonth() {
        val newCal = _selectedDate.value.clone() as Calendar
        newCal.add(Calendar.MONTH, -1)
        _selectedDate.value = newCal
        loadData()
    }

    private val _deleteState = MutableStateFlow<Boolean?>(null)
    val deleteState = _deleteState.asStateFlow()

    fun deleteWallet() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteWallet(walletId)
                .onSuccess { _deleteState.value = true }
                .onFailure { _deleteState.value = false }
            _isLoading.value = false
        }
    }

    fun resetDeleteState() {
        _deleteState.value = null
    }
}
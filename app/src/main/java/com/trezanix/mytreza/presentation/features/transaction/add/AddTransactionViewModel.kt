package com.trezanix.mytreza.presentation.features.transaction.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trezanix.mytreza.domain.model.Wallet
import com.trezanix.mytreza.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {

    var amount = MutableStateFlow("")
    var description = MutableStateFlow("")
    var transactionType = MutableStateFlow("EXPENSE")
    var selectedDate = MutableStateFlow(Date())
    var selectedWallet = MutableStateFlow<Wallet?>(null)
    var selectedCategory = MutableStateFlow<String?>(null)
    var sourceWallet = MutableStateFlow<Wallet?>(null)
    var targetWallet = MutableStateFlow<Wallet?>(null)
    var adminFee = MutableStateFlow("")

    private val _uiState = MutableStateFlow<AddTransactionState>(AddTransactionState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _wallets = MutableStateFlow<List<Wallet>>(emptyList())
    val wallets = _wallets.asStateFlow()

    init {
        loadWallets()
    }

    private fun loadWallets() {
        viewModelScope.launch {
            repository.getWallets()
                .onSuccess { list ->
                    _wallets.value = list
                    if (list.isNotEmpty()) {
                        selectedWallet.value = list[0]
                        sourceWallet.value = list[0]
                        if (list.size > 1) targetWallet.value = list[1]
                    }
                }
                .onFailure {
                    _uiState.value = AddTransactionState.Error("Gagal memuat dompet")
                }
        }
    }

    fun saveTransaction() {
        val type = transactionType.value
        val nominal = amount.value.toDoubleOrNull() ?: 0.0
        val desc = description.value
        val dateStr = selectedDate.value.toInstant().toString()

        if (nominal <= 0) {
            _uiState.value = AddTransactionState.Error("Nominal harus lebih dari 0")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddTransactionState.Loading

            val result = if (type == "TRANSFER") {
                val from = sourceWallet.value
                val to = targetWallet.value
                val fee = adminFee.value.toDoubleOrNull() ?: 0.0

                if (from == null || to == null) {
                    Result.failure(Exception("Pilih dompet asal dan tujuan"))
                } else if (from.id == to.id) {
                    Result.failure(Exception("Dompet asal dan tujuan tidak boleh sama"))
                } else {
                    repository.createTransfer(
                        fromWalletId = from.id,
                        toWalletId = to.id,
                        amount = nominal,
                        adminFee = fee,
                        description = desc,
                        date = dateStr
                    )
                }
            } else {
                val wallet = selectedWallet.value
                val categoryId = selectedCategory.value

                if (wallet == null) {
                    Result.failure(Exception("Pilih dompet terlebih dahulu"))
                } else {
                    repository.createTransaction(
                        walletId = wallet.id,
                        categoryId = categoryId,
                        type = type,
                        amount = nominal,
                        description = desc,
                        date = dateStr
                    )
                }
            }

            result.onSuccess {
                _uiState.value = AddTransactionState.Success
            }
                .onFailure { e ->
                    _uiState.value = AddTransactionState.Error(e.message ?: "Gagal menyimpan")
                }
        }
    }

    sealed class AddTransactionState {
        object Idle : AddTransactionState()
        object Loading : AddTransactionState()
        object Success : AddTransactionState()
        data class Error(val message: String) : AddTransactionState()
    }
}
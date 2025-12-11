package com.trezanix.mytreza.presentation.features.wallet.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trezanix.mytreza.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.Date

@HiltViewModel
class AddWalletViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {

    var walletName = MutableStateFlow("")
    var walletType = MutableStateFlow("BANK")
    var initialBalance = MutableStateFlow("")
    var selectedColor = MutableStateFlow("#2196F3")
    var selectedIcon = MutableStateFlow("wallet_default")

    private val _date = MutableStateFlow(Date())
    val date = _date.asStateFlow()

    private val _uiState = MutableStateFlow<AddWalletUiState>(AddWalletUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun saveWallet(onSuccess: () -> Unit) {
        val name = walletName.value
        val type = walletType.value
        val balanceVal = initialBalance.value.toDoubleOrNull()
        val color = selectedColor.value
        val icon = selectedIcon.value

        if (name.isBlank()) {
            _uiState.value = AddWalletUiState.Error("Nama dompet tidak boleh kosong")
            return
        }

        if (initialBalance.value.isNotEmpty() && balanceVal == null) {
            _uiState.value = AddWalletUiState.Error("Format saldo salah")
            return
        }

        val finalBalance = balanceVal ?: 0.0

        viewModelScope.launch {
            _uiState.value = AddWalletUiState.Loading

            repository.createWallet(name, type, finalBalance, color, icon)
                .onSuccess {
                    _uiState.value = AddWalletUiState.Success
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.value = AddWalletUiState.Error(e.message ?: "Gagal")
                }
        }
    }

    sealed class AddWalletUiState {
        object Idle : AddWalletUiState()
        object Loading : AddWalletUiState()
        object Success : AddWalletUiState()
        data class Error(val message: String) : AddWalletUiState()
    }
}
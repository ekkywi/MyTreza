package com.trezanix.mytreza.presentation.features.wallet.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trezanix.mytreza.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditWalletViewModel @Inject constructor(
    private val repository: WalletRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val walletId: String = checkNotNull(savedStateHandle["walletId"])

    var walletName = MutableStateFlow("")
    var selectedColor = MutableStateFlow("#2196F3")
    var selectedIcon = MutableStateFlow("wallet_default")

    var currentBalance = MutableStateFlow(0.0)
    var currentType = MutableStateFlow("BANK")

    private val _uiState = MutableStateFlow<EditState>(EditState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadWalletData()
    }

    private fun loadWalletData() {
        viewModelScope.launch {
            repository.getWalletDetail(walletId)
                .onSuccess { wallet ->
                    walletName.value = wallet.name
                    selectedColor.value = wallet.color ?: "#2196F3"
                    selectedIcon.value = wallet.icon ?: "wallet_default"

                    currentBalance.value = wallet.balance
                    currentType.value = wallet.type

                    _uiState.value = EditState.Idle
                }
                .onFailure {
                    _uiState.value = EditState.Error("Gagal memuat data dompet")
                }
        }
    }

    fun updateWallet(onSuccess: () -> Unit) {
        val name = walletName.value
        val color = selectedColor.value
        val icon = selectedIcon.value

        if (name.isBlank()) {
            _uiState.value = EditState.Error("Nama tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            _uiState.value = EditState.Loading
            repository.updateWallet(walletId, name, color, icon)
                .onSuccess {
                    _uiState.value = EditState.Success
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.value = EditState.Error(e.message ?: "Gagal update")
                }
        }
    }

    sealed class EditState {
        object Idle : EditState()
        object Loading : EditState()
        object Success : EditState()
        data class Error(val message: String) : EditState()
    }
}
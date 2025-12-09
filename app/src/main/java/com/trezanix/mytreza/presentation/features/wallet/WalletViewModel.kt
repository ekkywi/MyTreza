package com.trezanix.mytreza.presentation.features.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trezanix.mytreza.domain.model.Wallet
import com.trezanix.mytreza.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {

    private val _state = MutableStateFlow<WalletState>(WalletState.Loading)
    val state = _state.asStateFlow()

    init {
        loadWallets()
    }

    fun loadWallets() {
        viewModelScope.launch {
            _state.value = WalletState.Loading
            repository.getWallets()
                .onSuccess { wallets ->
                    _state.value = WalletState.Success(wallets)
                }
                .onFailure { exception ->
                    _state.value = WalletState.Error(exception.message ?: "Terjadi kesalahan")
                }
        }
    }

    sealed class WalletState {
        object Loading : WalletState()
        data class Success(val wallets: List<Wallet>) : WalletState()
        data class Error(val message: String) : WalletState()
    }
}
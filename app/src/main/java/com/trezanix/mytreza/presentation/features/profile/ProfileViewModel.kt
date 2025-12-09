package com.trezanix.mytreza.presentation.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trezanix.mytreza.data.local.TokenManager
import com.trezanix.mytreza.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    val userName = tokenManager.userName.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Memuat..."
    )

    val userEmail = tokenManager.userEmail.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "..."
    )

    private val _deleteState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val deleteState = _deleteState.asStateFlow()

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clearAuthData()
            onLogoutComplete()
        }
    }

    fun deleteAccount(confirmationInput: String, onSuccess: () -> Unit) {
        val currentEmail = userEmail.value ?: ""

        if (confirmationInput != currentEmail) {
            _deleteState.value = UiState.Error("Email konfirmasi tidak cocok!")
            return
        }

        viewModelScope.launch {
            _deleteState.value = UiState.Loading

            val result = authRepository.deleteAccount()

            result.onSuccess {
                _deleteState.value = UiState.Success(true)
                tokenManager.clearAuthData()
                onSuccess()
            }.onFailure { e ->
                _deleteState.value = UiState.Error(e.message ?: "Gagal hapus akun")
            }
        }
    }

    sealed class UiState<out T> {
        object Idle : UiState<Nothing>()
        object Loading : UiState<Nothing>()
        data class Success<T>(val data: T) : UiState<T>()
        data class Error(val message: String) : UiState<Nothing>()
    }
}
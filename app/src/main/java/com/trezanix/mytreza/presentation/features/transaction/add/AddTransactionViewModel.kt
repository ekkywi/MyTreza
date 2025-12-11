package com.trezanix.mytreza.presentation.features.transaction.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trezanix.mytreza.domain.model.Category
import com.trezanix.mytreza.domain.model.Wallet
import com.trezanix.mytreza.domain.repository.CategoryRepository
import com.trezanix.mytreza.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // --- STATE VARIABLES (Flow) ---
    val amount = MutableStateFlow("")
    val note = MutableStateFlow("")
    val date = MutableStateFlow(Date())

    // Tipe Transaksi: INCOME, EXPENSE, TRANSFER
    val transactionType = MutableStateFlow("EXPENSE")

    // Pilihan User
    val selectedWalletId = MutableStateFlow<String?>(null)
    val selectedCategoryId = MutableStateFlow<String?>(null)

    // Khusus Transfer
    val selectedSourceWalletId = MutableStateFlow<String?>(null)
    val selectedTargetWalletId = MutableStateFlow<String?>(null)
    val adminFee = MutableStateFlow("")

    // Data List untuk Dropdown
    private val _wallets = MutableStateFlow<List<Wallet>>(emptyList())
    val wallets = _wallets.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    // Status UI (Loading/Success/Error)
    private val _uiState = MutableStateFlow<AddTransactionUiState>(AddTransactionUiState.Idle)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Load Wallets
            walletRepository.getWallets().onSuccess { _wallets.value = it }
            // Load Categories
            categoryRepository.getCategories().onSuccess { _categories.value = it }
        }
    }

    // --- FUNGSI SIMPAN TRANSAKSI (INCOME / EXPENSE) ---
    fun saveTransaction() {
        val currentAmount = amount.value.toDoubleOrNull()
        val currentWalletId = selectedWalletId.value
        val currentType = transactionType.value
        val timeZone = java.util.TimeZone.getDefault()
        val offset = timeZone.getOffset(date.value.time)
        val adjustedDate = Date(date.value.time + offset)
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(adjustedDate)
        val currentNote = note.value
        val currentCategoryId = selectedCategoryId.value

        if (currentAmount == null || currentAmount <= 0) {
            _uiState.value = AddTransactionUiState.Error("Jumlah harus lebih dari 0")
            return
        }
        if (currentWalletId == null) {
            _uiState.value = AddTransactionUiState.Error("Pilih dompet terlebih dahulu")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddTransactionUiState.Loading

            // Panggil Repository (Parameter sudah sesuai)
            walletRepository.createTransaction(
                walletId = currentWalletId,
                amount = currentAmount,
                type = currentType,
                date = currentDate,
                description = currentNote,
                categoryId = currentCategoryId
            ).onSuccess {
                _uiState.value = AddTransactionUiState.Success
            }.onFailure { e ->
                _uiState.value = AddTransactionUiState.Error(e.message ?: "Gagal menyimpan transaksi")
            }
        }
    }

    // --- FUNGSI SIMPAN TRANSFER ---
    fun saveTransfer() {
        val currentAmount = amount.value.toDoubleOrNull()
        val sourceId = selectedSourceWalletId.value
        val targetId = selectedTargetWalletId.value
        val timeZone = java.util.TimeZone.getDefault()
        val offset = timeZone.getOffset(date.value.time)
        val adjustedDate = Date(date.value.time + offset)
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(adjustedDate)
        val currentNote = note.value
        val currentAdminFee = adminFee.value.toDoubleOrNull() ?: 0.0

        if (currentAmount == null || currentAmount <= 0) {
            _uiState.value = AddTransactionUiState.Error("Jumlah harus lebih dari 0")
            return
        }
        if (sourceId == null || targetId == null) {
            _uiState.value = AddTransactionUiState.Error("Pilih dompet asal dan tujuan")
            return
        }
        if (sourceId == targetId) {
            _uiState.value = AddTransactionUiState.Error("Dompet asal dan tujuan tidak boleh sama")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddTransactionUiState.Loading

            // Panggil Repository (Parameter sudah sesuai)
            walletRepository.createTransfer(
                sourceWalletId = sourceId,
                targetWalletId = targetId,
                amount = currentAmount,
                date = currentDate,
                description = currentNote,
                adminFee = currentAdminFee
            ).onSuccess {
                _uiState.value = AddTransactionUiState.Success
            }.onFailure { e ->
                _uiState.value = AddTransactionUiState.Error(e.message ?: "Gagal melakukan transfer")
            }
        }
    }

    fun resetState() {
        _uiState.value = AddTransactionUiState.Idle
    }
}

// Sealed Class untuk Status UI
sealed class AddTransactionUiState {
    object Idle : AddTransactionUiState()
    object Loading : AddTransactionUiState()
    object Success : AddTransactionUiState()
    data class Error(val message: String) : AddTransactionUiState()
}
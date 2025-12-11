package com.trezanix.mytreza.presentation.features.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trezanix.mytreza.domain.model.Category
import com.trezanix.mytreza.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            // Memanggil fungsi getCategories yang sudah ada di Repository Anda
            repository.getCategories().onSuccess {
                _categories.value = it
            }.onFailure {
                // Handle error jika perlu
            }
            _isLoading.value = false
        }
    }

    fun createCategory(name: String, type: String, icon: String, color: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            // --- MULAI SIMULASI (Mocking) ---
            // Karena kita belum buat endpoint createCategory di Backend/Repo,
            // kita pura-pura sukses dulu agar UI bisa dites.

            delay(1000) // Pura-pura loading 1 detik

            // Tambahkan kategori baru ke list lokal sementara biar kelihatan update
            val newCategory = Category(
                id = UUID.randomUUID().toString(),
                name = name,
                type = type,
                icon = icon,
                color = color,
                userId = "temp",
                createdAt = ""
            )

            // Update state lokal
            val currentList = _categories.value.toMutableList()
            currentList.add(newCategory)
            _categories.value = currentList

            onSuccess()
            // --- SELESAI SIMULASI ---

            /* NANTI: Kalau Backend sudah siap, hapus blok simulasi di atas
               dan uncomment kode di bawah ini:

               repository.createCategory(name, type, icon, color)
                   .onSuccess {
                       loadCategories() // Reload dari server
                       onSuccess()
                   }
                   .onFailure {
                       // Show error toast
                   }
            */

            _isLoading.value = false
        }
    }
}
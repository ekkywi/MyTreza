package com.trezanix.mytreza.presentation.features.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trezanix.mytreza.domain.model.Category
import com.trezanix.mytreza.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository
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

            repository.createCategory(name, type, icon, color)
                .onSuccess { newCategory ->
                    _categories.value += newCategory
                    onSuccess()
                }
                .onFailure {
                    // Handle error
                }

            _isLoading.value = false
        }
    }

    fun updateCategory(id: String, name: String, type: String, icon: String, color: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateCategory(id, name, type, icon, color)
                .onSuccess { 
                    // Optimistically update with local values to ensure UI reflects user changes
                    // even if backend return is stale
                    _categories.value = _categories.value.map { category ->
                         if (category.id == id) {
                             category.copy(
                                 name = name,
                                 type = type,
                                 icon = icon,
                                 color = color
                             )
                         } else {
                             category
                         }
                    }
                    onSuccess()
                }
                .onFailure {
                    // Handle error
                }
            _isLoading.value = false
        }
    }

    fun deleteCategory(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteCategory(id)
                .onSuccess {
                    loadCategories()
                    onSuccess()
                }
                .onFailure {
                    // Handle error
                }
            _isLoading.value = false
        }
    }
}
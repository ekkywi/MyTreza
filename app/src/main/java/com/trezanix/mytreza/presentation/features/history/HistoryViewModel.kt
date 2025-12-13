package com.trezanix.mytreza.presentation.features.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trezanix.mytreza.data.remote.dto.TransactionDto
import com.trezanix.mytreza.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _allTransactions = MutableStateFlow<List<TransactionDto>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _filterType = MutableStateFlow("ALL")
    private val _isLoading = MutableStateFlow(false)

    val isLoading = _isLoading.asStateFlow()
    val displayedTransactions = combine(
        _allTransactions,
        _searchQuery,
        _filterType
    ) { transactions, query, type ->
        transactions.filter { trx ->
            val matchesType = type == "ALL" || trx.type == type
            val matchesSearch = if (query.isBlank()) true else {
                (trx.description?.contains(query, ignoreCase = true) == true) ||
                        (trx.category?.name?.contains(query, ignoreCase = true) == true) ||
                        (trx.wallet?.name?.contains(query, ignoreCase = true) == true)
            }

            matchesType && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchQuery = _searchQuery.asStateFlow()
    val filterType = _filterType.asStateFlow()

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getTransactions(limit = 100)
                .onSuccess { _allTransactions.value = it }
                .onFailure { /* Handle error */ }
            _isLoading.value = false
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFilterChange(newType: String) {
        _filterType.value = newType
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteTransaction(id)
                .onSuccess {
                    loadTransactions() // Reload data after deletion
                }
                .onFailure {
                    // Handle error if needed
                }
            _isLoading.value = false
        }
    }
}
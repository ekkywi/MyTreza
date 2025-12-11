package com.trezanix.mytreza.presentation.features.analysis

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trezanix.mytreza.data.remote.dto.TransactionDto
import com.trezanix.mytreza.domain.model.Transaction
import com.trezanix.mytreza.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {

    // 1. State Tanggal yang Dipilih (Default: Hari ini)
    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate = _selectedDate.asStateFlow()

    // 2. State Data Statistik (Untuk Chart)
    private val _categoryStats = MutableStateFlow<List<CategoryStat>>(emptyList())
    val categoryStats = _categoryStats.asStateFlow()

    // 3. State Ringkasan (Income vs Expense)
    private val _summary = MutableStateFlow(FinancialSummary())
    val summary = _summary.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Cache semua transaksi agar tidak perlu fetch ulang terus saat ganti bulan (Client-side filtering)
    private var allCachedTransactions: List<Transaction> = emptyList()

    init {
        loadAnalysisData()
    }

    // Fungsi Ganti Bulan
    fun changeMonth(increment: Int) {
        val calendar = Calendar.getInstance()
        calendar.time = _selectedDate.value
        calendar.add(Calendar.MONTH, increment)
        _selectedDate.value = calendar.time

        // PENTING: Proses ulang data yang sudah ada dengan tanggal baru
        if (allCachedTransactions.isNotEmpty()) {
            processTransactions(allCachedTransactions, _selectedDate.value)
        } else {
            loadAnalysisData()
        }
    }

    fun loadAnalysisData() {
        viewModelScope.launch {
            _isLoading.value = true

            // Ambil BANYAK data (limit 1000) agar bisa difilter di sisi aplikasi
            repository.getTransactions(page = 1, limit = 1000).onSuccess { transactions ->

                // 1. Konversi ke Domain
                val domainTransactions = transactions.map { it.toDomain() }

                // 2. Simpan di Cache Memory
                allCachedTransactions = domainTransactions

                // 3. Proses Filter & Kalkulasi
                processTransactions(domainTransactions, _selectedDate.value)

            }.onFailure {
                Log.e("AnalysisViewModel", "Gagal load data: ${it.message}")
            }

            _isLoading.value = false
        }
    }

    private fun processTransactions(allTrx: List<Transaction>, selectedDate: Date) {
        // Tentukan Target Bulan & Tahun dari selectedDate
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate
        val targetMonth = calendar.get(Calendar.MONTH) // 0 = Januari
        val targetYear = calendar.get(Calendar.YEAR)

        Log.d("AnalysisDebug", "FILTERING START: Target Bulan=$targetMonth, Tahun=$targetYear")

        // ---------------------------------------------------------
        // LANGKAH 1: FILTER BY DATE (Hanya ambil transaksi bulan ini)
        // ---------------------------------------------------------
        val transactionsInMonth = allTrx.filter { trx ->
            try {
                // Parser Tanggal (Sesuai Logcat: 2025-12-03T07:00:00.000Z)
                val parserISO = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                val parserSimple = SimpleDateFormat("yyyy-MM-dd", Locale.US)

                val trxDate = try {
                    parserISO.parse(trx.date)
                } catch (e: Exception) {
                    parserSimple.parse(trx.date)
                }

                if (trxDate != null) {
                    val trxCal = Calendar.getInstance()
                    trxCal.time = trxDate

                    // Bandingkan Bulan & Tahun
                    val isSameMonth = trxCal.get(Calendar.MONTH) == targetMonth
                    val isSameYear = trxCal.get(Calendar.YEAR) == targetYear

                    isSameMonth && isSameYear
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }

        Log.d("AnalysisDebug", "FILTER RESULT: Ditemukan ${transactionsInMonth.size} transaksi di bulan terpilih")


        // ---------------------------------------------------------
        // LANGKAH 2: BUSINESS LOGIC (Pisahkan Transfer & Admin Fee)
        // ---------------------------------------------------------

        fun isTransfer(t: Transaction): Boolean {
            return t.categoryName.contains("Transfer", ignoreCase = true)
        }

        // Pisahkan data
        val realTransactions = transactionsInMonth.filter { !isTransfer(it) }
        val transferTransactions = transactionsInMonth.filter { isTransfer(it) }

        // Hitung Biaya Admin (Selisih Transfer Keluar - Transfer Masuk)
        val totalTransferExpense = transferTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        val totalTransferIncome = transferTransactions.filter { it.type == "INCOME" }.sumOf { it.amount }

        // Admin Fee = Uang Keluar Transfer - Uang Masuk Transfer
        val adminFee = kotlin.math.max(0.0, totalTransferExpense - totalTransferIncome)


        // ---------------------------------------------------------
        // LANGKAH 3: SUMMARY CALCULATION
        // ---------------------------------------------------------

        // Income Murni (Tanpa Transfer Masuk)
        val totalRealIncome = realTransactions
            .filter { it.type == "INCOME" }
            .sumOf { it.amount }

        // Expense Murni + Biaya Admin
        val totalRealExpense = realTransactions
            .filter { it.type == "EXPENSE" }
            .sumOf { it.amount } + adminFee

        _summary.value = FinancialSummary(totalRealIncome, totalRealExpense)


        // ---------------------------------------------------------
        // LANGKAH 4: PIE CHART DATA
        // ---------------------------------------------------------

        // Grouping Expense Murni
        val groupedList = realTransactions
            .filter { it.type == "EXPENSE" }
            .groupBy { it.categoryName }
            .map { (category, list) ->
                CategoryStat(category, list.sumOf { it.amount }, getRandomColor())
            }
            .toMutableList()

        // Tambahkan Biaya Admin ke Chart jika ada
        if (adminFee > 0) {
            groupedList.add(
                CategoryStat(
                    categoryName = "Biaya Admin/Transfer",
                    total = adminFee,
                    colorHex = "#9E9E9E" // Abu-abu
                )
            )
        }

        // Sort Descending
        val finalStats = groupedList.sortedByDescending { it.total }

        _categoryStats.value = finalStats
    }

    // Helper Warna Random
    private fun getRandomColor(): String {
        val colors = listOf(
            "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5",
            "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50",
            "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800", "#FF5722"
        )
        return colors.random()
    }
}

// --- DATA CLASSES & EXTENSION ---

data class CategoryStat(
    val categoryName: String,
    val total: Double,
    val colorHex: String
)

data class FinancialSummary(
    val income: Double = 0.0,
    val expense: Double = 0.0
) {
    val net: Double get() = income - expense
}

fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        id = this.id,
        amount = this.amount,
        description = this.description,
        date = this.date,
        type = this.type,
        walletId = this.wallet?.id ?: "",
        categoryId = this.category?.id,
        categoryName = this.category?.name ?: "Umum",
        walletName = this.wallet?.name ?: "Dompet"
    )
}
package com.trezanix.mytreza.domain.repository

import com.trezanix.mytreza.data.remote.dto.TransactionDto
import com.trezanix.mytreza.domain.model.Transaction
import com.trezanix.mytreza.domain.model.Wallet
import com.trezanix.mytreza.domain.model.WalletStats
import com.trezanix.mytreza.domain.model.Category

interface WalletRepository {
    // Wallet CRUD
    suspend fun createWallet(
        name: String,
        type: String,
        balance: Double,
        color: String,
        icon: String
    ): Result<Wallet>

    suspend fun getWallets(): Result<List<Wallet>>
    suspend fun getWalletDetail(id: String): Result<Wallet>
    suspend fun updateWallet(id: String, name: String, color: String, icon: String): Result<Wallet>
    suspend fun archiveWallet(id: String): Result<Boolean>
    suspend fun deleteWallet(id: String): Result<Boolean>

    // Stats
    suspend fun getWalletStats(id: String, month: Int, year: Int): Result<WalletStats>

    // Transactions Global (Dashboard) - Masih pakai DTO
    suspend fun getTransactions(page: Int, limit: Int): Result<List<TransactionDto>>

    // Transactions Specific Wallet (Detail) - Pakai Domain Transaction
    // PASTIKAN NAMANYA 'getTransactionsByWallet' (Bukan getWalletTransactions)
    suspend fun getTransactionsByWallet(
        id: String,
        month: Int,
        year: Int
    ): Result<List<Transaction>>

    suspend fun createTransaction(
        walletId: String,
        amount: Double,
        type: String,
        date: String,
        description: String?,
        categoryId: String?
    ): Result<Transaction>

    suspend fun createTransfer(
        sourceWalletId: String,
        targetWalletId: String,
        amount: Double,
        date: String,
        description: String?,
        adminFee: Double = 0.0
        // Admin fee kita skip dulu biar compile jalan
    ): Result<Boolean>

    suspend fun deleteTransaction(id: String): Result<Boolean>

    suspend fun updateTransaction(
        id: String,
        amount: Double,
        type: String,
        date: String,
        description: String?,
        categoryId: String?,
        walletId: String
    ): Result<Transaction>

    suspend fun getTransactionById(id: String): Result<Transaction>

    suspend fun getCategories(): Result<List<Category>>

    suspend fun createCategory(name: String, type: String, icon: String, color: String): Result<Boolean>
}
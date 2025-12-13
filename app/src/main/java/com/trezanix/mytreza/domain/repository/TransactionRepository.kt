package com.trezanix.mytreza.domain.repository

import com.trezanix.mytreza.data.remote.dto.TransactionDto

interface TransactionRepository {
    suspend fun getTransactions(page: Int = 1, limit: Int = 10): Result<List<TransactionDto>>

    suspend fun createTransaction(
        amount: Double,
        type: String,
        description: String,
        categoryId: String,
        walletId: String,
        date: String
    ): Result<Boolean>

    suspend fun deleteTransaction(id: String): Result<Boolean>
}
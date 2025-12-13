package com.trezanix.mytreza.data.repository

import android.util.Log
import com.trezanix.mytreza.data.remote.api.MyTrezaApiService
import com.trezanix.mytreza.data.remote.dto.CreateTransactionRequest
import com.trezanix.mytreza.data.remote.dto.TransactionDto
import com.trezanix.mytreza.domain.repository.TransactionRepository
import com.trezanix.mytreza.data.repository.TransactionRepositoryImpl
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val api: MyTrezaApiService
) : TransactionRepository {

    override suspend fun getTransactions(page: Int, limit: Int): Result<List<TransactionDto>> {
        return try {
            val response = api.getTransactions(page, limit)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data?.items ?: emptyList()
                Result.success(data)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTransaction(
        amount: Double,
        type: String,
        description: String,
        categoryId: String,
        walletId: String,
        date: String
    ): Result<Boolean> {
        return try {
            val request = CreateTransactionRequest(
                walletId = walletId,
                amount = amount,
                type = type,
                date = date,
                description = description,
                categoryId = categoryId
            )
            val response = api.createTransaction(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception("Gagal membuat transaksi"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTransaction(id: String): Result<Boolean> {
        return try {
            val response = api.deleteTransaction(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception("Gagal menghapus transaksi"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
package com.trezanix.mytreza.data.repository

import com.trezanix.mytreza.data.remote.api.MyTrezaApiService
import com.trezanix.mytreza.data.remote.dto.CreateTransactionRequest
import com.trezanix.mytreza.data.remote.dto.CreateTransferRequest
import com.trezanix.mytreza.data.remote.dto.CreateWalletRequest
import com.trezanix.mytreza.data.remote.dto.DailyStatsDto
import com.trezanix.mytreza.data.remote.dto.TransactionDto
import com.trezanix.mytreza.data.remote.dto.UpdateWalletRequest
import com.trezanix.mytreza.domain.model.Wallet
import com.trezanix.mytreza.domain.repository.WalletRepository
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val api: MyTrezaApiService
) : WalletRepository {

    override suspend fun getWallets(): Result<List<Wallet>> {
        return try {
            val response = api.getWallets()

            if (response.isSuccessful && response.body()?.success == true) {
                val walletData = response.body()?.data
                val walletItems = walletData?.items ?: emptyList()
                Result.success(walletItems.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createWallet(name: String, type: String, balance: Double, color: String, icon: String): Result<Wallet> {
        return try {
            val request = CreateWalletRequest(name, type, balance, color, icon)
            val response = api.createWallet(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                if (data != null) Result.success(data.toDomain())
                else Result.failure(Exception("Data kosong"))
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateWallet(id: String, name: String, color: String, icon: String): Result<Boolean> {
        return try {
            val request = UpdateWalletRequest(name, color, icon)
            val response = api.updateWallet(id, request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWalletDetail(id: String): Result<Wallet> {
        return try {
            val response = api.getWalletDetail(id)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                if (data != null) Result.success(data.toDomain())
                else Result.failure(Exception("Data dompet kosong"))
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWalletTransactions(id: String, month: Int, year: Int): Result<List<TransactionDto>> {
        return try {
            val response = api.getTransactionsByWallet(id, month, year)
            if (response.isSuccessful && response.body()?.success == true) {
                val items = response.body()?.data?.items ?: emptyList()
                Result.success(items)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWalletDailyStats(id: String, month: Int, year: Int): Result<List<DailyStatsDto>> {
        return try {
            val response = api.getWalletDailyStats(id, month, year)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: emptyList()
                Result.success(data)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWalletStats(id: String, month: Int, year: Int): Result<com.trezanix.mytreza.domain.model.WalletStats> {
        return try {
            val response = api.getWalletDailyStats(id, month, year)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: emptyList()
                val income = data.sumOf { it.income }
                val expense = data.sumOf { it.expense }
                Result.success(
                    com.trezanix.mytreza.domain.model.WalletStats(
                        income = income,
                        expense = expense,
                        total = income - expense
                    )
                )
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteWallet(id: String): Result<Boolean> {
        return try {
            val response = api.deleteWallet(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTransaction(
        walletId: String,
        categoryId: String?,
        type: String,
        amount: Double,
        description: String,
        date: String
    ): Result<Boolean> {
        return try {
            val request = CreateTransactionRequest(
                walletId = walletId,
                categoryId = categoryId,
                type = type,
                amount = amount,
                description = description,
                date = date
            )
            val response = api.createTransaction(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTransfer(
        fromWalletId: String,
        toWalletId: String,
        amount: Double,
        adminFee: Double,
        description: String,
        date: String
    ): Result<Boolean> {
        return try {
            val request = CreateTransferRequest(
                fromWalletId = fromWalletId,
                toWalletId = toWalletId,
                amount = amount,
                adminFee = adminFee,
                description = description,
                date = date
            )
            val response = api.createTransfer(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
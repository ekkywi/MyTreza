package com.trezanix.mytreza.data.repository

import com.trezanix.mytreza.data.remote.api.MyTrezaApiService
import com.trezanix.mytreza.data.remote.dto.CreateWalletRequest
import com.trezanix.mytreza.data.remote.dto.TransactionDto
import com.trezanix.mytreza.data.remote.dto.UpdateWalletRequest
import com.trezanix.mytreza.data.remote.dto.WalletDto
// --- IMPORT TAMBAHAN YANG WAJIB ADA ---
import com.trezanix.mytreza.data.remote.dto.CreateTransactionRequest
import com.trezanix.mytreza.data.remote.dto.CreateTransferRequest
// --------------------------------------
import com.trezanix.mytreza.domain.model.Transaction
import com.trezanix.mytreza.domain.model.Wallet
import com.trezanix.mytreza.domain.model.WalletStats
import com.trezanix.mytreza.domain.model.Category
import com.trezanix.mytreza.domain.repository.WalletRepository
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val api: MyTrezaApiService
) : WalletRepository {
    // ... (sisanya sama persis dengan yang Anda kirimkan, tidak perlu diubah) ...
    // ... Paste isi fungsi-fungsinya di sini ...

    // --- 1. CREATE ---
    override suspend fun createWallet(name: String, type: String, balance: Double, color: String, icon: String): Result<Wallet> {
        return try {
            val request = CreateWalletRequest(
                name = name,
                type = type,
                initialBalance = balance,
                color = color,
                icon = icon
            )
            val response = api.createWallet(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!.toDomain())
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 2. GET ALL ---
    override suspend fun getWallets(): Result<List<Wallet>> {
        return try {
            val response = api.getWallets()
            if (response.isSuccessful && response.body()?.success == true) {
                val items = response.body()?.data?.items ?: emptyList()
                Result.success(items.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 3. GET DETAIL ---
    override suspend fun getWalletDetail(id: String): Result<Wallet> {
        return try {
            val response = api.getWalletDetail(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!.toDomain())
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 4. UPDATE ---
    override suspend fun updateWallet(id: String, name: String, color: String, icon: String): Result<Wallet> {
        return try {
            val request = UpdateWalletRequest(name = name, color = color, icon = icon)
            val response = api.updateWallet(id, request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!.toDomain())
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 5. ARCHIVE ---
    override suspend fun archiveWallet(id: String): Result<Boolean> {
        return try {
            val response = api.archiveWallet(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.body()?.message ?: response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 6. DELETE ---
    override suspend fun deleteWallet(id: String): Result<Boolean> {
        return try {
            val response = api.deleteWallet(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.body()?.message ?: response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 7. STATS ---
    override suspend fun getWalletStats(id: String, month: Int, year: Int): Result<WalletStats> {
        return try {
            val response = api.getWalletStats(id, month, year)
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()!!.data!!
                Result.success(
                    WalletStats(
                        totalIncome = dto.totalIncome,
                        totalExpense = dto.totalExpense,
                        netBalance = dto.netBalance
                    )
                )
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 7. TRANSACTIONS (GLOBAL / DASHBOARD) ---
    override suspend fun getTransactions(page: Int, limit: Int): Result<List<TransactionDto>> {
        return try {
            val response = api.getTransactions(page, limit)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data?.items ?: emptyList())
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 8. TRANSACTIONS (BY WALLET / DETAIL) ---
    override suspend fun getTransactionsByWallet(id: String, month: Int, year: Int): Result<List<Transaction>> {
        return try {
            val response = api.getTransactionsByWallet(walletId = id, month = month, year = year)
            if (response.isSuccessful && response.body()?.success == true) {
                val items = response.body()?.data?.items ?: emptyList()
                Result.success(items.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTransaction(
        walletId: String,
        amount: Double,
        type: String,
        date: String,
        description: String?,
        categoryId: String? // <--- TAMBAHAN PARAMETER
    ): Result<Transaction> {
        return try {
            val request = CreateTransactionRequest(
                walletId = walletId,
                amount = amount,
                type = type,
                date = date,
                description = description,
                categoryId = categoryId // <--- PASSING KE DTO
            )
            // ... (sisanya sama)
            val response = api.createTransaction(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!.toDomain())
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTransfer(
        sourceWalletId: String,
        targetWalletId: String,
        amount: Double,
        date: String,
        description: String?,
        adminFee: Double
    ): Result<Boolean> {
        return try {
            val request = CreateTransferRequest(
                // SESUAIKAN NAMA PARAMETER DENGAN DTO
                sourceWalletId = sourceWalletId,
                targetWalletId = targetWalletId,
                amount = amount,
                date = date,
                description = description,
                adminFee = adminFee
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

    // --- MAPPERS ---
    private fun WalletDto.toDomain(): Wallet {
        return Wallet(
            id = this.id,
            name = this.name,
            type = this.type,
            balance = this.balance,
            color = this.color,
            icon = this.icon,
            accountNumber = ""
        )
    }

    private fun TransactionDto.toDomain(): Transaction {
        return Transaction(
            id = this.id,
            amount = this.amount,
            description = this.description,
            date = this.date,
            type = this.type,
            categoryName = this.category?.name ?: "Umum",
            walletName = this.wallet?.name ?: "Dompet",
            categoryId = this.categoryId,
            walletId = this.walletId,
            categoryIcon = this.category?.icon
        )
    }

    override suspend fun deleteTransaction(id: String): Result<Boolean> {
        return try {
            val response = api.deleteTransaction(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                val errorMsg = response.body()?.message ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTransaction(
        id: String,
        amount: Double,
        type: String,
        date: String,
        description: String?,
        categoryId: String?,
        walletId: String
    ): Result<Transaction> {
        return try {
            val request = com.trezanix.mytreza.data.remote.dto.UpdateTransactionRequest(
                amount = amount,
                type = type,
                date = date,
                description = description,
                categoryId = categoryId,
                walletId = walletId
            )
            val response = api.updateTransaction(id, request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!.toDomain())
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 9. GET TRANSACTION BY ID ---
    override suspend fun getTransactionById(id: String): Result<Transaction> {
        // Karena API tidak punya endpoint khusus GET /transactions/{id},
        // Kita gunakan endpoint list dan filter manual (Temporary Workaround)
        // Idealnya: Minta Backend Engineer buat endpoint GET /transactions/{id}
        return try {
            val response = api.getTransactions(page = 1, limit = 100) // Ambil cukup banyak biar ketemu
            if (response.isSuccessful && response.body()?.success == true) {
                val items = response.body()?.data?.items ?: emptyList()
                val found = items.find { it.id == id }
                if (found != null) {
                    Result.success(found.toDomain())
                } else {
                    Result.failure(Exception("Transaksi tidak ditemukan"))
                }
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategories(): Result<List<Category>> {
        // TODO: Nanti ganti dengan API Call asli: api.getCategories()
        // Sementara kita return list kosong atau dummy biar tidak error
        return try {
            // Jika Anda sudah punya endpoint, pakai ini:
            // val response = api.getCategories()
            // Result.success(response.data.map { it.toDomain() })

            // Untuk sekarang agar bisa compile ViewModel:
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createCategory(name: String, type: String, icon: String, color: String): Result<Boolean> {
        // TODO: Nanti sambungkan ke API
        return Result.success(true)
    }
}
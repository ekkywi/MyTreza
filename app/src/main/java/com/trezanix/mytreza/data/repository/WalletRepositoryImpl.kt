package com.trezanix.mytreza.data.repository

import com.trezanix.mytreza.data.remote.api.MyTrezaApiService
import com.trezanix.mytreza.data.remote.dto.CreateWalletRequest
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

    override suspend fun createWallet(name: String, type: String, balance: Double): Result<Wallet> {
        return try {
            val request = CreateWalletRequest(name, type, balance)
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

}
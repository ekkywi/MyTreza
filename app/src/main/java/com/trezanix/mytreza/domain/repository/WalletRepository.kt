package com.trezanix.mytreza.domain.repository

import com.trezanix.mytreza.data.remote.dto.DailyStatsDto
import com.trezanix.mytreza.data.remote.dto.TransactionDto
import com.trezanix.mytreza.domain.model.Wallet

interface WalletRepository {
    suspend fun getWallets(): Result<List<Wallet>>
    suspend fun createWallet(name: String, type: String, balance: Double, color: String, icon: String): Result<Wallet>
    suspend fun getWalletDetail(id: String): Result<Wallet>
    suspend fun getWalletTransactions(id: String, month: Int, year: Int): Result<List<TransactionDto>>
    suspend fun getWalletDailyStats(id: String, month: Int, year: Int): Result<List<DailyStatsDto>>
    suspend fun getWalletStats(id: String, month: Int, year: Int): Result<com.trezanix.mytreza.domain.model.WalletStats>
    suspend fun deleteWallet(id: String): Result<Boolean>

    suspend fun updateWallet(id: String, name: String, color: String, icon: String): Result<Boolean>
}
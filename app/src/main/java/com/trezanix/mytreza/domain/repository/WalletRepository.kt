package com.trezanix.mytreza.domain.repository

import com.trezanix.mytreza.domain.model.Wallet

interface WalletRepository {
    suspend fun getWallets(): Result<List<Wallet>>
    suspend fun createWallet(name: String, type: String, balance: Double): Result<Wallet>
}
package com.trezanix.mytreza.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WalletStatsDto(
    @SerializedName("walletId")
    val walletId: String,

    @SerializedName("totalIncome")
    val totalIncome: Double,

    @SerializedName("totalExpense")
    val totalExpense: Double,

    @SerializedName("netBalance")
    val netBalance: Double
)
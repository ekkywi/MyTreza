package com.trezanix.mytreza.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DailyStatsDto(
    @SerializedName("date")
    val date: String,

    @SerializedName("income")
    val income: Double,

    @SerializedName("expense")
    val expense: Double
)
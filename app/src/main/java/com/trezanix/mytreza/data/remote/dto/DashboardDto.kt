package com.trezanix.mytreza.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DashboardDto(
    @SerializedName("netWorth") val netWorth: Double,
    @SerializedName("incomeThisMonth") val incomeThisMonth: Double,
    @SerializedName("expenseThisMonth") val expenseThisMonth: Double,
    @SerializedName("biggestSpendingCategory") val biggestSpendingCategory: CategorySpendingDto?
)

data class CategorySpendingDto(
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: Double
)
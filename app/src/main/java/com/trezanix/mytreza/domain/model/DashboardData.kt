package com.trezanix.mytreza.domain.model

data class DashboardData(
    val userName: String,
    val netWorth: Double,
    val income: Double,
    val expense: Double,
    val biggestCategory: String,
    val biggestCategoryAmount: Double
)
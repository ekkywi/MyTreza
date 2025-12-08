package com.trezanix.mytreza.domain.model

data class DashboardData(
    val netWorth: Double,
    val income: Double,
    val expense: Double,
    val biggestCategory: String,
    val biggestCategoryAmount: Double
)
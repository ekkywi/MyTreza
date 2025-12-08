package com.trezanix.mytreza.domain.repository

import com.trezanix.mytreza.domain.model.DashboardData

interface DashboardRepository {
    suspend fun getDashboardSummary(): Result<DashboardData>
}